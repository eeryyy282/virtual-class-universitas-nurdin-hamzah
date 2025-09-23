pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Virtual Class Universitas Nurdin Hamzah"
include(":app")
include(":core")
include(":onboarding")
include(":authentication")
include(":student")
include(":lecturer")
include(":profilesettings")
include(":course")
include(":enrollcourse")
include(":assignment")
include(":submission")
