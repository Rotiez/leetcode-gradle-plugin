package dev.rotiez.leetcode.gradle.plugin.client.common.graphql.request

class ProblemListRequest(
    private val categorySlug: String = "all-code-essentials",
    private val limit: Int,
    private val skip: Int = 0,
    private val filters: Map<String, Any>?
) {
    fun toGraphQLRequest(): GraphQLRequest {
        val query = """
            query problemsetQuestionList(
                ${'$'}categorySlug: String, 
                ${'$'}limit: Int, 
                ${'$'}skip: Int, 
                ${'$'}filters: QuestionListFilterInput
            ) {
                problemsetQuestionList: questionList(
                    categorySlug: ${'$'}categorySlug
                    limit: ${'$'}limit
                    skip: ${'$'}skip
                    filters: ${'$'}filters
                ) {
                    total: totalNum
                    questions: data {
                        acRate
                        difficulty
                        freqBar
                        frontendQuestionId: questionFrontendId
                        isFavor
                        paidOnly: isPaidOnly
                        status
                        title
                        titleSlug
                        topicTags {
                            name
                            id
                            slug
                        }
                        hasSolution
                        hasVideoSolution
                    }
                }
            }
        """.trimIndent()

        return GraphQLRequest(
            operationName = "problemsetQuestionList",
            query = query,
            variables = mapOf(
                "categorySlug" to categorySlug,
                "limit" to limit,
                "skip" to skip
            ).plus(filters?.let { mapOf("filters" to it) } ?: emptyMap())
        )
    }
}
