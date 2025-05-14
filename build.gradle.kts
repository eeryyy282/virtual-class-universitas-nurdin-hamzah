// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
    id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
}