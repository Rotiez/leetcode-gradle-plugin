package dev.rotiez.leetcode.gradle.plugin.client.model

data class ProblemDetails(
    val id: Long,
    val title: String,
    val difficulty: String,
    val description: String,
    val codeSnippets: List<CodeSnippet>,
    val topicTags: List<TopicTag>,
    val hints: List<String>,
    val url: String,
)


data class NextData(
    val props: PropsWrapper
)

data class PropsWrapper(
    val pageProps: PageProps
)

data class PageProps(
    val dehydratedState: DehydratedState
)

data class DehydratedState(
    val queries: List<Query>
)

data class Query(
    val state: QueryState
)

data class QueryState(
    val data: QueryData
)

data class QueryData(
    val question: Question? = null
)

data class Question(
    val title: String,
    val questionId: String,
    val questionFrontendId: String,
    val questionTitle: String,
    val difficulty: String,
    val content: String,
    val codeSnippets: List<CodeSnippet>,
    val topicTags: List<TopicTag>,
    val hints: List<String>,
)

data class TopicTag(
    val name: String,
    val slug: String,
)

data class CodeSnippet(
    val code: String,
    val lang: String,
    val langSlug: String
)
