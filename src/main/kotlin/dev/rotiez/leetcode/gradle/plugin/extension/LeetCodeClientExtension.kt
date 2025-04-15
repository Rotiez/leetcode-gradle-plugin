package dev.rotiez.leetcode.gradle.plugin.extension

import org.gradle.api.provider.Property

interface LeetCodeClientExtension {
    val baseUrl: Property<String>
}