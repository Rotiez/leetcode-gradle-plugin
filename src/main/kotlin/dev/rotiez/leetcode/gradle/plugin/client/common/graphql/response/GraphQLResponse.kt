package dev.rotiez.leetcode.gradle.plugin.client.common.graphql.response

data class GraphQLResponse<T>(
    val data: T?
)