package tolibib.delfi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import tolibib.cleanHtml
import tolibib.domain.Book
import tolibib.downloadCover
import tolibib.parsePages
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

private val httpClient = buildOkHttpClient()
private val mapper = jacksonObjectMapper()

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
	val rawDescription = product["description"] as String
	val description = cleanHtml(rawDescription)
	val publisher = product["publisher"] as String
	val isbn = product["barcode"] as String

	val pagesRaw = attributes.find { it is Map<*, *> && it["k"] == "numberOfPages" }
		?.let { it as Map<*, *> }
		?.get("v")

	val pages = parsePages(pagesRaw)

	val authors = (product["authors"] as List<*>)
		.map { (it as Map<*, String>)["authorName"] }
		.joinToString(", ") { it as String }

	val image = product["images"] as Map<*, *>
	val coverXxl = image["xxl"] as String
	val coverXl = image["xl"] as String
	val coverL = image["l"] as String
	val cover = if (coverXxl != "") coverXxl else if (coverXl != "") coverXl else coverL
	val coverFile = downloadCover("https://delfi.rs$cover")

	println("Book $bookId parsed: $title")

	return Book(
		title = title,
		authors = authors,
		description = description,
		publisher = publisher,
		isbn13 = isbn,
		pages = pages,
		cover = coverFile
	)
}
