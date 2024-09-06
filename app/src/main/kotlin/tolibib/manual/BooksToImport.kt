package tolibib.manual

import tolibib.domain.Book
import tolibib.downloadCover

val book1 = Book(
	title = "Večnost",
	description = "",
	publisher = "Polaris",
	pages = 348,
	authors = "Greg Ber",
	isbn13 = "",
	cover = downloadCover("https://cdn.antikvarne-knjige.com/knjige/storage/app/uploads/public/ak-/ima/ges/thumb_494472_0_640_0_0_auto.jpg")
)