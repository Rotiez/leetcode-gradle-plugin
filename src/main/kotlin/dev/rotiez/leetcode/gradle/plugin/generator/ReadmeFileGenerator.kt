package dev.rotiez.leetcode.gradle.plugin.generator

import dev.rotiez.leetcode.gradle.plugin.client.model.ProblemDetails
import java.io.File

class ReadmeFileGenerator: FileGenerator {

    companion object {
        private const val README_NAME = "README.md"
    }

    override fun generateFile(dir: File, packageName: String, problem: ProblemDetails) {
        val readmeFile = File(dir, README_NAME)
        readmeFile.writeText(
            """
                |## ```${problem.id}``` ${problem.title}
                |
                |### 📌 Details
                |- Difficulty: ```${problem.difficulty}```
                |- Topics: ${problem.topicTags.joinToString(", ") { "```${it.name}```" }}
                |
                |### 📄 Description
                |${problem.description}
                |
                |### 💡 Hints
                |${problem.hints.joinToString("\n") { "- $it" }}
                |
                |### 🔗 Links
                |- [Problem page](${problem.url})
                |
            """.trimMargin()
        )
    }
}