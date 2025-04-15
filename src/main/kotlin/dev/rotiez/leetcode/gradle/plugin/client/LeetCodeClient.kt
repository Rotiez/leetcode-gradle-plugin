package dev.rotiez.leetcode.gradle.plugin.client

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.DefaultGraphQLClient
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.request.ProblemListRequest
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response.GraphQLResponse
import dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response.ProblemListResponse
import dev.rotiez.leetcode.gradle.plugin.client.common.rest.DefaultRestClient
import dev.rotiez.leetcode.gradle.plugin.client.model.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.gradle.internal.impldep.kotlinx.serialization.json.JsonObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class LeetCodeClient(
    baseUrl: String,
) {

    private val httpClient = OkHttpClient()
    private val httpUrl = baseUrl.toHttpUrl()

    private val restClient = DefaultRestClient(httpUrl, httpClient)
    private val graphQLClient = DefaultGraphQLClient(restClient)

    fun getProblemDetail(id: Long, lang: ProblemLanguage): ProblemDetails = getProblemDetail(listOf(id), lang).first()

    fun getProblemDetail(ids: List<Long>, lang: ProblemLanguage): List<ProblemDetails> {
        val graphQLResponse: GraphQLResponse<ProblemListResponse> = graphQLClient.execute(
            request = ProblemListRequest(
                limit = ids.size,
                filters = mapOf("searchKeywords" to ids)
            ).toGraphQLRequest(),
            responseType = ProblemListResponse::class.java
        )

        val questions = graphQLResponse.data?.problemsetQuestionList?.questions
            ?: throw RuntimeException("Problems not found")

        return questions.map { question ->
            try {
                val restResponse: String = restClient.get(path = "/problems/${question.titleSlug}/description/")

                val document = Jsoup.parse(restResponse)
                val fullDescription = document
                    .select("meta[name=description]").attr("content")
                    .replace("Can you solve this real interview question?", "")
                    .trim()

                val scriptElement: Element = document.select("script#__NEXT_DATA__").first()
                    .also { println(it) }
                    ?:throw RuntimeException("Script not found")

                val codeSnippet = try {
                    val jsonText = scriptElement.data()

                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter: JsonAdapter<NextData> = moshi.adapter(NextData::class.java)

                    val pageProps = jsonAdapter.fromJson(jsonText)
                    val codeSnippets = pageProps
                        ?.props
                        ?.pageProps
                        ?.dehydratedState
                        ?.queries
                        ?.firstOrNull()
                        ?.state
                        ?.data
                        ?.question
                        ?.also { println("question: $it") }
                        ?.codeSnippets

                    codeSnippets?.find { it.lang == lang.langName }
                } catch (e: Exception) {
                    throw RuntimeException("Failed to parse code snippet", e)
                }

                ProblemDetails(
                    id = question.frontendQuestionId.toLong(),
                    difficulty = question.difficulty,
                    description = fullDescription,
                    codeSnippet = CodeSnippet(
                        code = codeSnippet?.code,
                        lang = codeSnippet?.lang,
                    )
                )
            } catch (e: Exception) {
                throw RuntimeException("Failed to fetch problem description for ${question.titleSlug}", e)
            }
        }
    }
}
