package tolibib.okh

import okhttp3.Interceptor
import okhttp3.Response

class CookiesInterceptor : Interceptor {
	private val cookies = mutableMapOf<String, String>()

	override fun intercept(chain: Interceptor.Chain): Response {
		val builder = chain.request().newBuilder()

		// add cookies to request
		for (cookie in cookies) {
			builder.addHeader("Cookie", cookie.value)
		}

		val originalResponse = chain.proceed(builder.build())

		// read cookies from response
		originalResponse.headers("Set-Cookie").forEach {
			cookies[nameOf(it)] = it
		}

		return originalResponse
	}

	private fun nameOf(cookie: String): String {
		return cookie.split("=")[0]
	}

}