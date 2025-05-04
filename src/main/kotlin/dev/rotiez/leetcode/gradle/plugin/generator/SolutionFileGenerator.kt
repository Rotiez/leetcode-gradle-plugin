package dev.rotiez.leetcode.gradle.plugin.generator

import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage
import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage.KOTLIN
import dev.rotiez.leetcode.gradle.plugin.client.ProblemLanguage.JAVA
import dev.rotiez.leetcode.gradle.plugin.client.model.CodeSnippet
import dev.rotiez.leetcode.gradle.plugin.client.model.ProblemDetails
import dev.rotiez.leetcode.gradle.plugin.generator.templates.JavaSolution
import dev.rotiez.leetcode.gradle.plugin.generator.templates.KotlinSolution
import dev.rotiez.leetcode.gradle.plugin.generator.templates.SolutionFile
import java.io.File

class SolutionFileGenerator: FileGenerator {

    companion object {
        private const val SOLUTION_NAME = "Solution"
    }

    override fun generateFile(dir: File, packageName: String, problem: ProblemDetails) {
        val lang = determineLang(dir)
        val template = getTemplate(lang)
        val code = getCodeFromSnippets(lang, problem.codeSnippets)

        val solutionFile = File(dir, template.getFileName(SOLUTION_NAME))
        solutionFile.writeText(buildString {
            appendLine(template.getPackageName(packageName))
            appendLine()
            appendLine(code)
        })
    }

    private fun determineLang(dir: File): ProblemLanguage {
        return when {
            dir.path.contains("kotlin", ignoreCase = true) -> KOTLIN
            dir.path.contains("java", ignoreCase = true) -> JAVA
            else -> throw IllegalArgumentException("Cannot determine language from directory path: ${dir.path}")
        }
    }

    private fun getTemplate(lang: ProblemLanguage): SolutionFile {
        return when (lang) {
            KOTLIN -> KotlinSolution
            JAVA -> JavaSolution
        }
    }

    private fun getCodeFromSnippets(lang: ProblemLanguage, codeSnippets: List<CodeSnippet>): String =
        codeSnippets.find { it.langSlug == lang.slug }?.code.orEmpty()
}