package dev.rotiez.leetcode.gradle.plugin.client.common.rest

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jsoup.Connection.Method
import org.jsoup.Connection.Method.POST
import org.jsoup.Connection.Method.GET
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.reflect.Type

class DefaultRestClient(
    private val httpUrl: HttpUrl,
    private val httpClient: OkHttpClient,
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
) {

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
    }

    fun <T> post(path: String, body: RequestBody, responseType: Type): T {
        val request = customizeRequest(POST, path, body)
        return callRequest(request, responseType)
    }

    fun <T> get(path: String, responseType: Type): T {
        val request = customizeRequest(GET, path)
        return callRequest(request, responseType)
    }

    fun get(path: String): String {
        val request = customizeRequest(GET, path)
        return callRequestForString(request)
    }

    private fun customizeRequest(method: Method, path: String, body: RequestBody? = null): Request {
        val requestBuilder = Request.Builder()
            .url(httpUrl.newBuilder().encodedPath(path).build())
            .addHeader("User-Agent", USER_AGENT)
            .addHeader("Referer", "https://leetcode.com/")
            .addHeader("Origin", "https://leetcode.com")
            .addHeader("Content-Type", "application/json")

        when (method) {
            GET -> requestBuilder.get()
            POST -> requestBuilder.post(body ?: throw IllegalArgumentException("POST method requires a body"))
            else -> throw IllegalArgumentException("Unsupported HTTP method: ${method.name}")
        }

        return requestBuilder.build()
    }

    private fun <T> callRequest(request: Request, responseType: Type): T {
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val responseBody = response.body?.string()
                ?: throw IOException("Empty response body")

            val adapter = moshi.adapter<T>(responseType)
            return adapter.fromJson(responseBody)
                ?: throw IOException("Failed to parse response")
        }
    }

    private fun callRequestForString(request: Request): String {
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            return response.body?.string()
                ?: throw IOException("Empty response body")
        }
    }
}
