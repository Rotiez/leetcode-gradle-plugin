package dev.rotiez.leetcode.gradle.plugin.generator

import dev.rotiez.leetcode.gradle.plugin.client.model.ProblemDetails
import java.io.File

interface FileGenerator {
    fun generateFile(dir: File, packageName: String, problem: ProblemDetails)
}
