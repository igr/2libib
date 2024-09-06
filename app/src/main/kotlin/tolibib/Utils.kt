package tolibib

import io.github.cdimascio.dotenv.Dotenv
import okhttp3.Request
import tolibib.domain.User
import java.io.File
import java.nio.file.Files

fun cleanHtml(html: String): String = html.replace("<[^>]*>".toRegex(), "").trim()
fun parsePages(pagesRaw: Any?): Int =
	when (pagesRaw) {
		is String -> pagesRaw.toInt()
		is Int -> pagesRaw
		else -> 0
	}

fun downloadCover(url: String): File {
	val request = Request.Builder()
		.url(url)
		.get()
		.build()

	httpClient.newCall(request).execute().use { response ->
		if (!response.isSuccessful) throw Exception("Unexpected code $response")
		val body = response.body!!.bytes()
		val file = File.createTempFile("cover", ".jpg")
		file.deleteOnExit()
		Files.write(file.toPath(), body)
		println("Cover downloaded")
		return file
	}
}

fun loadUserPass(dotenv: Dotenv): User {
	val username = dotenv["USERNAME"]
	val password = dotenv["PASSWORD"]
	println("Username: $username")
	return User(username, password)
}