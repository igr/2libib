package tolibib

fun cleanHtml(html: String): String = html.replace("<[^>]*>".toRegex(), "").trim()