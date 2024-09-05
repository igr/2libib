package tolibib

import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import tolibib.domain.Book
import tolibib.domain.User
import tolibib.okh.CookiesInterceptor
import java.io.IOException

private val client = buildOkHttpClient()
private const val baseUrl = "https://www.libib.com"
private fun u(path: String) = "$baseUrl$path"

private fun buildOkHttpClient() = OkHttpClient.Builder()
	.followRedirects(true)
	.followSslRedirects(true)
	.addInterceptor(CookiesInterceptor())
	.build()

fun loginToLibib(user: User) {

	// step 1
	val formBody1 = FormBody.Builder()
		.add("login-email", user.username)
		.build()

	val request1 = Request.Builder()
		.url(u("/home/login-pre-fetch/submit"))
		.post(formBody1)
		.build()

	client.newCall(request1).execute().use { response ->
		if (!response.isSuccessful) throw IOException("Unexpected code $response")
	}

	// step 2
	val formBody2 = FormBody.Builder()
		.add("login-email", user.username)
		.add("login-password", user.password)
		.add("login-remember-me", "")
		.build()

	val request2 = Request.Builder()
		.url(u("/home/login/submit"))
		.post(formBody2)
		.build()

	client.newCall(request2).execute().use { response ->
		if (!response.isSuccessful) throw IOException("Unexpected code $response")
	}
	println("Logged into Libib as ${user.username}")
}

fun addBookToLibib(book: Book) {
	val formBody = MultipartBody.Builder().setType(MultipartBody.FORM)
		.addFormDataPart("manual-entry-library-select", "1970279")          // collection "INBOX"
		.addFormDataPart("manual-entry-type", "book")
		.addFormDataPart("title", book.title)
		.addFormDataPart("creators", book.authors)
		.addFormDataPart("description", book.description)
		.addFormDataPart("publisher", book.publisher)
		.addFormDataPart("publish_year", "")
		.addFormDataPart("publish_month", "")
		.addFormDataPart("publish_day", "")
		.addFormDataPart("ean_isbn13", book.isbn13)
		.addFormDataPart("upc_isbn10", "")
		.addFormDataPart("price", "")
		.addFormDataPart("tags", "")
		.addFormDataPart("group", "")
		.addFormDataPart("notes", "")
		.addFormDataPart("length_of", book.pages.toString())


	if (book.cover != null) {
		formBody.addFormDataPart(
			"image-0",
			book.cover!!.name,
			book.cover!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
		)
	}

	val request = Request.Builder()
		.url(u("/library/manual-entry/submit"))
		.post(formBody.build())
		.build()

	client.newCall(request).execute().use { response ->
		if (!response.isSuccessful) throw IOException("Unexpected code $response")
	}

	println("Added book '${book.title}' to Libib")
}