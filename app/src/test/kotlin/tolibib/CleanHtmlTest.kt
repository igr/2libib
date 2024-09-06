package tolibib

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CleanHtmlTest {

	@Test
	fun test_simple_html_clean() {
		assertEquals("hello", cleanHtml("<p>hello</p>"))
		assertEquals("hello", cleanHtml("<div id='123'>hello</p>"))
		assertEquals("hello", cleanHtml("<div id=\"123\">  hello </p>  "))
	}
}