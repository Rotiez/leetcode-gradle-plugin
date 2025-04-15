plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

group = "dev.rotiez"
version = "1.0.0"

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("org.jsoup:jsoup:1.19.1")
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("leetcodePlugin") {
            id = "dev.rotiez.leetcode-plugin"
            implementationClass = "dev.rotiez.leetcode.gradle.plugin.LeetCodePlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>("maven") {}
    }
}
