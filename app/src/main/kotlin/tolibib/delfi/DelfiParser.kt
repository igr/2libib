package tolibib.delfi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import tolibib.domain.Book
import java.io.File
import java.nio.file.Files
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

private val httpClient = buildOkHttpClient()
val mapper = jacksonObjectMapper()

private fun buildOkHttpClient() = OkHttpClient.Builder()
	.followRedirects(true)
	.followSslRedirects(true)
	.callTimeout(Duration.of(30, SECONDS))
	.build()

fun parseDelfiBookPage(bookId: Int): Book {
	val request = Request.Builder()
		.url("https://delfi.rs/api/pc-frontend-api/overview/$bookId")
		.header("User-Agent", "Mozilla/5.0")
		.header("Accept", "application/json")
		.header("referer", "https://delfi.rs")
		.get()
		.build()
	httpClient.newCall(request).execute().use { response ->
		if (!response.isSuccessful) throw Exception("Unexpected code $response")
		val body = response.body!!.string()
		return parseBody(bookId, body)
	}
}

private fun parseBody(bookId: Int, json: String): Book {
	val map = mapper.readValue(json, Map::class.java)
	val data = map["data"] as Map<*, *>
	val product = data["product"] as Map<*, *>
	val attributes = product["attributes"] as List<*>

	// elements

	val title = product["title"] as String
	val description = product["description"] as String
	val publisher = product["publisher"] as String
	val isbn = product["barcode"] as String

	val pages = attributes.find { it is Map<*, *> && it["k"] == "numberOfPages" }
		?.let { it as Map<*, *> }
		?.get("v") as? Int ?: 0

	val authors = (product["authors"] as List<*>)
		.map{(it as Map<*, String>)["authorName"]}
		.joinToString(", ") { it as String }

	val image = product["images"] as Map<*, *>
	val cover = image["xxl"] as String
	val coverFile = downloadCover(bookId, cover)

	println("Book $bookId parsed: $title")

	return Book(
		title = title,
		authors = authors,
		description = cleanHtml(description),
		publisher = publisher,
		isbn13 = isbn,
		pages = pages,
		cover = coverFile
	)
}

private fun cleanHtml(html: String): String = html.replace("<.*?>", "")

private fun downloadCover(bookdId: Int, url: String): File {
	val request = Request.Builder()
		.url("https://delfi.rs$url")
		.get()
		.build()

	httpClient.newCall(request).execute().use { response ->
		if (!response.isSuccessful) throw Exception("Unexpected code $response")
		val body = response.body!!.bytes()
		val file = File("cover-$bookdId.jpg")
		Files.write(file.toPath(), body)
		return file
	}
}