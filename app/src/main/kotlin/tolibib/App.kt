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

    parseDelfiBookPage(184409).let {
        addBookToLibib(it)
    }
    parseDelfiBookPage(188708).let {
        addBookToLibib(it)
    }

}



