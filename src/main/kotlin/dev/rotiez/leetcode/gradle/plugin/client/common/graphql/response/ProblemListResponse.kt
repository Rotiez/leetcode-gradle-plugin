package dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response

data class ProblemListResponse(
    val problemsetQuestionList: QuestionList
) {
    data class QuestionList(
        val total: Int,
        val questions: List<Question>
    )

    data class Question(
        val acRate: Double,
        val difficulty: String,
        val freqBar: String?,
        val frontendQuestionId: String,
        val isFavor: Boolean,
        val paidOnly: Boolean,
        val status: String?,
        val title: String,
        val titleSlug: String,
        val topicTags: List<TopicTag>,
        val hasSolution: Boolean,
        val hasVideoSolution: Boolean
    )

    data class TopicTag(
        val name: String,
        val id: String,
        val slug: String
    )
}
