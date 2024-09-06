package tolibib.goodread

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
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

fun parseGoodreadBookPage(bookUrl: String): Book {
	val request = Request.Builder()
		.url(bookUrl)
		.get()
		.build()

	httpClient.newCall(request).execute().use { response ->
		if (!response.isSuccessful) throw Exception("Unexpected code $response")
		val body = response.body!!.string()
		return parseBody(body)
	}
}

private fun parseBody(html: String): Book {
	// block 1
	val start = "<script type=\"application/ld+json\">"
	val index = html.indexOf(start)
	val end = html.indexOf("</script>", index)
	val block = html.substring(index + start.length, end)

	val ld = mapper.readValue(block, Map::class.java)

	val title = ld["name"] as String
	val numberOfPages = ld["numberOfPages"] as Int
	val pages = parsePages(numberOfPages)
	val imageUrl = ld["image"] as String
	val cover = downloadCover(imageUrl)

	val authors = ld["author"] as List<Map<String, String>>
	val author = authors[0]["name"] as String

	// block 2
	val start2 = "<script id=\"__NEXT_DATA__\" type=\"application/json\">"
	val index2 = html.indexOf(start2)
	val end2 = html.indexOf("</script>", index2)
	val block2 = html.substring(index2 + start2.length, end2)
	val nd = mapper.readValue(block2, Map::class.java)
	val props = nd["props"] as Map<String, Any>
	val pageProps = props["pageProps"] as Map<String, Any>
	val apolloState = pageProps["apolloState"] as Map<String, Any>
	val book = apolloState["Book:kca://book/amzn1.gr.book.v1.fo4n7K2pyvTDErytPVnwwQ"] as Map<String, Any>
	val description = book["description"] as String
	val details = book["details"] as Map<String, Any>
	val publisher = details["publisher"] as String
	val isbn13 = details["isbn13"] as String?

	println("Book parsed: $title")

	return Book(
		title = title,
		authors = author,
		description = description,
		publisher = publisher,
		isbn13 = isbn13 ?: "",
		pages = pages,
		cover = cover
	)
}

