package tolibib

import io.github.cdimascio.dotenv.Dotenv
import tolibib.delfi.parseDelfiBookPage
import tolibib.domain.User

fun loadUserPass(dotenv: Dotenv): User {
	val username = dotenv["USERNAME"]
	val password = dotenv["PASSWORD"]
	println("Username: $username")
	return User(username, password)
}

fun main() {
	val dotenv = Dotenv.load()

	val user = loadUserPass(dotenv)

	loginToLibib(user)

	intArrayOf(
		11794, 37244, 11885, 37245, 37246, 182965, 10729, 10730
	).forEach {
		parseDelfiBookPage(it).run {
			addBookToLibib(this)
		}
	}

}
