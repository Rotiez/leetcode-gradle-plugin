package dev.rotiez.leetcode.gradle.plugin.extension

import org.gradle.api.provider.Property

interface LeetCodeGenerationExtension {
    val rebuildExisting: Property<Boolean>
}