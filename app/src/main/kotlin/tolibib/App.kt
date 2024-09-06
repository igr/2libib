package tolibib

import io.github.cdimascio.dotenv.Dotenv
import okhttp3.OkHttpClient
import tolibib.delfi.parseDelfiBookPage
import tolibib.goodread.parseGoodreadBookPage
import java.time.Duration
import java.time.temporal.ChronoUnit.SECONDS

// stateless http client
val httpClient = OkHttpClient.Builder()
	.followRedirects(true)
	.followSslRedirects(true)
	.callTimeout(Duration.of(30, SECONDS))
	.build()

// 🔥 DELFI.RS ids
val defi: IntArray = intArrayOf(
	4050
)

// 🔥 GOODREADS urls
val goodreads: Array<String> = arrayOf(
)

fun main() {
	val dotenv = Dotenv.load()

	val user = loadUserPass(dotenv)

	loginToLibib(user)

	defi.forEach {
		parseDelfiBookPage(it).run {
			addBookToLibib(this)
		}
	}

	goodreads.forEach {
		parseGoodreadBookPage(it).run {
			addBookToLibib(this)
		}
	}
}
