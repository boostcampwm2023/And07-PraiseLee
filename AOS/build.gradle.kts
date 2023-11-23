plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        val nav_version = "2.7.5"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.6.1")
    }
}
