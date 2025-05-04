package dev.rotiez.leetcode.gradle.plugin.generator.templates

object KotlinSolution : SolutionFile {
    override val fileExt = "kt"
    override fun getPackageName(pkg: String): String = "package $pkg"
}
