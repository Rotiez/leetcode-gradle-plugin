package dev.rotiez.leetcode.gradle.plugin.client

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.DefaultGraphQLClient
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.request.ProblemListRequest
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response.GraphQLResponse
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response.ProblemListResponse
import dev.rotiez.leetcode.gradle.plugin.client.common.rest.DefaultRestClient
import dev.rotiez.leetcode.gradle.plugin.client.model.NextData
import dev.rotiez.leetcode.gradle.plugin.client.model.ProblemDetails
import dev.rotiez.leetcode.gradle.plugin.client.model.Question
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.jsoup.Jsoup

class LeetCodeClient(
    private val baseUrl: String,
) {

    private val httpClient = OkHttpClient()
    private val httpUrl = baseUrl.toHttpUrl()

    private val restClient = DefaultRestClient(httpUrl, httpClient)
    private val graphQLClient = DefaultGraphQLClient(restClient)

    fun getProblemDetail(id: Long): ProblemDetails? = getProblemDetail(listOf(id)).firstOrNull()

    fun getProblemDetail(ids: List<Long>): List<ProblemDetails> {
        val graphQLResponse: GraphQLResponse<ProblemListResponse> = graphQLClient.execute(
            request = ProblemListRequest(
                limit = ids.size,
                filters = mapOf("searchKeywords" to ids)
            ).toGraphQLRequest(),
            responseType = ProblemListResponse::class.java
        )

        val questions = graphQLResponse.data?.problemsetQuestionList?.questions
            ?: throw RuntimeException("Problems not found")

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<NextData> = moshi.adapter(NextData::class.java)

        return questions.mapNotNull { questionGraphQl ->
            val requestPath = "/problems/${questionGraphQl.titleSlug}/description/"

            try {
                val restResponse: String = restClient.get(path = requestPath)

                val scriptElement = Jsoup
                    .parse(restResponse)
                    .select("script#__NEXT_DATA__")
                    .first() ?: throw RuntimeException("Script not found")

                val question: Question? = try {
                    val jsonText = scriptElement.data()
                    val pageProps = jsonAdapter.fromJson(jsonText)

                    pageProps?.props?.pageProps?.dehydratedState?.queries
                        ?.firstNotNullOfOrNull { it.state.data.question }

                } catch (e: Exception) {
                    throw RuntimeException("Failed to parse question details", e)
                }

                question?.let {
                    ProblemDetails(
                        id = it.questionFrontendId.toLong(),
                        title = it.questionTitle,
                        difficulty = it.difficulty,
                        description = it.content,
                        codeSnippets = it.codeSnippets,
                        hints = it.hints,
                        topicTags = it.topicTags,
                        url = baseUrl + requestPath
                    )
                }
            } catch (e: Exception) {
                throw RuntimeException(
                    "Failed to fetch problem description for titleSlug: ${questionGraphQl.titleSlug}", e
                )
            }
        }
    }
}
