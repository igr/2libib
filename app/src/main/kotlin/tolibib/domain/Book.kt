package tolibib.domain

import java.io.File

data class Book(
	val title: String,
	val authors: String,
	val description: String,
	val publisher: String,
	val isbn13: String,
	val pages: Int,
	var cover: File? = null
)