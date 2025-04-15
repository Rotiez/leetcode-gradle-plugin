package dev.rotiez.leetcode.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class RunSolutionTask: DefaultTask() {

    @TaskAction
    fun run() {
        println("Run")
    }
}
