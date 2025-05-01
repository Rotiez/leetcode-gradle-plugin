package dev.rotiez.leetcode.gradle.plugin.task

import dev.rotiez.leetcode.gradle.plugin.client.LeetCodeClient
import dev.rotiez.leetcode.gradle.plugin.generator.FileGenerator
import dev.rotiez.leetcode.gradle.plugin.generator.ReadmeFileGenerator
import dev.rotiez.leetcode.gradle.plugin.generator.SolutionFileGenerator
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

    @get:Option(
        option = "ids",
        description = "Comma-separated list of LeetCode problem IDs"
    )
    @get:Input
    abstract val problemIds: Property<String>

    @get:Option(
        option = "rebuild",
        description = "Rebuild existing templates if they already exist"
    )
    @get:Input
    abstract val rebuild: Property<Boolean>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val baseDir: DirectoryProperty

    @get:Internal
    abstract val client:  Property<LeetCodeClient>

    init {
        rebuild.convention(false)
    }

    @TaskAction
    fun generate() {
        val props = getProperties()
        val client = client.get()

        project.logger.lifecycle("Generating templates for problems: ${props.problemIds.joinToString(", ")}")

        val generators = getGenerators()
        val problemDetails = client.getProblemDetail(props.problemIds)

        project.logger.info("Fetched description for problems: ${props.problemIds.joinToString(", ")}")

        problemDetails.forEach { problem ->
            val solutionPackageName = "solution_${problem.id}"
            val packageName = "${props.packageName}.$solutionPackageName"
            val solutionDir = File(props.baseDir, packageName.replace(".", "/"))

            if (solutionDir.exists()) {
                if (props.rebuild) {
                    solutionDir.deleteRecursively()
                    solutionDir.mkdirs()
                    project.logger.lifecycle("Rebuilt directory for problem ${problem.id}")
                } else {
                    project.logger.lifecycle("Skipping problem ${problem.id} – directory already exists")
                    return@forEach
                }
            } else {
                if (solutionDir.mkdirs()) {
                    project.logger.info("Created directory '$solutionPackageName' for problem: ${problem.id}")
                } else {
                    throw GradleException("Failed to create directory: ${solutionDir.absolutePath}")
                }
            }

            generators.forEach {
                it.generateFile(solutionDir, packageName, problem)
            }
        }

        project.logger.lifecycle("Problem templates successfully generated")
    }

    private fun getProperties(): GenerateProblemTemplatesProperties {
        return GenerateProblemTemplatesProperties(
            problemIds = problemIds.get()
                .split(",")
                .mapNotNull { it.trim().toLongOrNull() }
                .takeIf { it.isNotEmpty() }
                ?: throw GradleException("You must specify valid problem IDs"),
            packageName = packageName.get(),
            baseDir = baseDir.get().asFile.path,
            rebuild = rebuild.get(),
        )
    }

    private fun getGenerators(): List<FileGenerator> {
        return listOf(
            ReadmeFileGenerator(),
            SolutionFileGenerator()
        )
    }

    data class GenerateProblemTemplatesProperties(
        val problemIds: List<Long>,
        val rebuild: Boolean,
        val packageName: String,
        val baseDir: String,
    )
}
