package dev.rotiez.leetcode.gradle.plugin.extension

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * Extension for configuring the LeetCode Gradle plugin.
 */
interface LeetCodePluginExtension {

    /**
     * Base package name where generated solution files will be placed.
     *
     * Example: `com.example.leetcode`
     */
    val packageName: Property<String>

    /**
     * The base directory where all generated templates and solutions will be stored.
     *
     * Example: `project.layout.projectDirectory.dir("src/main/kotlin")`
     */
    val baseDir: DirectoryProperty

    /**
     * Configuration for the LeetCode API client, such as base URL and authentication.
     */
    @get:Nested
    val client: LeetCodeClientExtension
}