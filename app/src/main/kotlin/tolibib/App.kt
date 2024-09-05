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

    intArrayOf(46950, 9842, 11576, 63643).forEach {
        parseDelfiBookPage(it).run {
            addBookToLibib(this)
        }
    }

}



