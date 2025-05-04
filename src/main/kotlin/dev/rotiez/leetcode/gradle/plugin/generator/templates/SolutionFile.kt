package dev.rotiez.leetcode.gradle.plugin.generator.templates

interface SolutionFile {
    val fileExt: String

    fun getPackageName(pkg: String): String
    fun getFileName(className: String) = "$className.$fileExt"
}
