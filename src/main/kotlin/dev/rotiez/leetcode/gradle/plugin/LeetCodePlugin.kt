package dev.rotiez.leetcode.gradle.plugin

import dev.rotiez.leetcode.gradle.plugin.client.LeetCodeClient
import dev.rotiez.leetcode.gradle.plugin.extension.LeetCodePluginExtension
import dev.rotiez.leetcode.gradle.plugin.task.GenerateProblemTemplatesTask
import dev.rotiez.leetcode.gradle.plugin.task.RunSolutionTask
import dev.rotiez.leetcode.gradle.plugin.task.SubmitSolutionTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class LeetCodePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("leetcode", LeetCodePluginExtension::class.java).apply {
            val defaultBaseDir = when {
                project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    project.layout.projectDirectory.dir("src/main/kotlin")
                }
                else -> {
                    project.layout.projectDirectory.dir("src/main/java")
                }
            }
            packageName.convention("dev.leetcode.solutions")
            baseDir.convention(defaultBaseDir)
            client.baseUrl.convention("https://leetcode.com")
            templateGeneration.rebuildExisting.convention(false)
        }

        registerTasks(project, extension)
    }

    private fun registerTasks(project: Project, extension: LeetCodePluginExtension) {
        val client = LeetCodeClient(
            baseUrl = extension.client.baseUrl.get(),
        )

        project.tasks.register("runSolution", RunSolutionTask::class.java) {
            group = TASK_GROUP
            description = "Run LeetCode solutions"
        }

        project.tasks.register("submitSolution", SubmitSolutionTask::class.java) {
            group = TASK_GROUP
            description = "Submit LeetCode solutions"
        }

        project.tasks.register("generateTemplates", GenerateProblemTemplatesTask::class.java) {
            group = TASK_GROUP
            description = "Generate LeetCode problem templates"

            this.client.set(client)
            this.packageName.set(extension.packageName)
            this.baseDir.set(extension.baseDir)
        }
    }

    companion object {
        private const val TASK_GROUP = "leetcode"
    }
}
