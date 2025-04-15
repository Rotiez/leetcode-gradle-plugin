package dev.rotiez.leetcode.gradle.plugin.client.model

data class ProblemDetails(
    val id: Long,
    val difficulty: String,
    val description: String,
    val codeSnippet: CodeSnippet,
)




data class NextData(
    val props: InnerProps
)

data class InnerProps(
    val pageProps: PageProps
)

data class PageProps(
    val dehydratedState: DehydratedState?
)

data class DehydratedState(
    val queries: List<Query>?
)

data class Query(
    val state: State?
)

data class State(
    val data: Data?
)

data class Data(
    val question: Question?
)

data class Question(
    val codeSnippets: List<CodeSnippet>?
)

data class CodeSnippet(
    val code: String?,
    val lang: String?,
)
