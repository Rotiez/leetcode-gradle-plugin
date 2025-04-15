package dev.rotiez.leetcode.gradle.plugin.client.common.graphql

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.request.GraphQLRequest
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response.GraphQLResponse
import dev.rotiez.leetcode.gradle.plugin.client.common.rest.DefaultRestClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

class DefaultGraphQLClient(
    private val httpClient: DefaultRestClient,
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
) {

    companion object {
        private const val GRAPHQL_PATH = "/graphql"
    }

    fun <T> execute(request: GraphQLRequest, responseType: Type): T {
        val adapter = moshi.adapter(GraphQLRequest::class.java)
        val json = adapter.toJson(request)

        return httpClient.post(
            path = GRAPHQL_PATH,
            body = json.toRequestBody("application/json".toMediaType()),
            responseType = Types.newParameterizedType(GraphQLResponse::class.java, responseType)
        )
    }
}
