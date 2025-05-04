package dev.rotiez.leetcode.gradle.plugin.generator.templates

object JavaSolution : SolutionFile {
    override val fileExt = "java"
    override fun getPackageName(pkg: String): String = "package $pkg;"
}
