package dev.rotiez.leetcode.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class SubmitSolutionTask: DefaultTask() {

    @TaskAction
    fun submit() {
        println("Submit")
    }
}