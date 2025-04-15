package dev.rotiez.leetcode.gradle.plugin.task

import dev.rotiez.leetcode.gradle.plugin.client.LeetCodeClient
import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage
import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage.KOTLIN
import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage.JAVA
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

abstract class GenerateProblemTemplatesTask: DefaultTask() {

    companion object {
        private const val README_NAME = "README.md"
    }

    @get:Option(
        option = "ids",
        description = "Comma-separated list of LeetCode problem IDs"
    )
    @get:Input
    abstract val problemIds: Property<String>

    @get:Input
    abstract val rebuildExisting: Property<Boolean>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val baseDir: DirectoryProperty

    @get:Internal
    abstract val client:  Property<LeetCodeClient>

    @TaskAction
    fun generate() {
        val problemIds = problemIds.get()
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .takeIf { it.isNotEmpty() }
            ?: throw GradleException("You must specify valid problem IDs")

        val packageName = packageName.get()
        val baseDir = baseDir.get().asFile.path
        val client = client.get()
        val rebuildExisting = rebuildExisting.get()

        project.logger.lifecycle("Generating templates for problems: ${problemIds.joinToString(", ")}")

        val packagePath = packageName.replace(".", "/")

        val problemLanguage = when {
            baseDir.contains("kotlin") -> KOTLIN
            baseDir.contains("java") -> JAVA
            else -> throw GradleException("Could not determine the programming language based on baseDir")
        }
        val problemDetails = client.getProblemDetail(problemIds, problemLanguage)

        project.logger.info("Fetched description for problems: ${problemIds.joinToString(", ")}")

        problemDetails.forEach { problem ->
            val solutionPackageName = "solution_${problem.id}"
            val solutionDir = File(baseDir, "$packagePath/$solutionPackageName")

            if (solutionDir.exists() && !rebuildExisting) {
                project.logger.lifecycle("Skipping problem ${problem.id} – directory already exists")
                return@forEach
            }

            if (solutionDir.mkdirs()) {
                project.logger.info("Created directory '$solutionPackageName' for problem: ${problem.id}")
            } else throw GradleException("Failed to create directory: ${solutionDir.absolutePath}")

            val readmeFile = File(solutionDir, README_NAME)
            readmeFile.writeText("""
                |# Problem ${problem.id}
                |
                |## Details
                |- **Difficulty**: ${problem.difficulty}
                |
                |## Description
                |${problem.description}
            """.trimMargin())

            project.logger.info("Generated README.md for problem ${problem.id} in ${readmeFile.path}")
        }

        project.logger.lifecycle("Problem templates successfully generated")
    }
}
