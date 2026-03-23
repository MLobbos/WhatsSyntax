pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WhatsSyntax"

// Android app
include(":app")

// Shared Kotlin models (Android + Backend)
include(":packages:shared-models")
project(":packages:shared-models").projectDir = file("packages/shared-models")

// Ktor backend (pure JVM, no Android)
include(":services:api")
project(":services:api").projectDir = file("services/api")
