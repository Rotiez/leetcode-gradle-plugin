package dev.rotiez.leetcode.gradle.plugin.client.common.graphql.request

data class GraphQLRequest(
    val operationName: String,
    val query: String,
    val variables: Map<String, Any>?
)