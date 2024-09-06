package tolibib

import io.github.cdimascio.dotenv.Dotenv
import okhttp3.OkHttpClient
import tolibib.delfi.parseDelfiBookPage
import tolibib.domain.Book
import tolibib.goodread.parseGoodreadBookPage
import tolibib.manual.book1
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

// 🔥 Manually created
val manualBooks: Array<Book> = arrayOf(
	book1
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

	manualBooks.forEach {
		addBookToLibib(it)
	}
}
