package dev.rotiez.leetcode.gradle.plugin.client

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LeetCodeClientTest {

    private val baseUrl = "https://leetcode.com"
    private val leetCodeClient = LeetCodeClient(baseUrl)

    @Test
    fun `test getProblemDescription with problemId 1`() {
        val problemDescription = leetCodeClient.getProblemDetail(1) ?: throw RuntimeException("Problem not found")

        assertNotNull(problemDescription)
        assertEquals(1L, problemDescription.id)
        assertNotNull(problemDescription.description)
        println(problemDescription)
    }
}