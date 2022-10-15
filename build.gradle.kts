@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.password
import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") apply false
}

allprojects {
    group = "me.func"
    version = "1.0.3"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        withSourcesJar()
    }

    tasks {
        withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
        withType<Jar>().configureEach { duplicatesStrategy = DuplicatesStrategy.EXCLUDE }
    }

    publishing {
        repositories {
            mavenLocal()
            maven {
                name = "func"
                url = uri(
                    "https://repo.c7x.dev/repository/maven-${
                        if (project.version.toString().contains("SNAPSHOT")) "snapshots" else "releases"
                    }"
                )
                credentials {
                    username = System.getenv("CRI_REPO_LOGIN") ?: System.getenv("CRISTALIX_REPO_USERNAME")
                    password = System.getenv("CRI_REPO_PASSWORD") ?: System.getenv("CRISTALIX_REPO_PASSWORD")
                }
            }
        }
    }
}