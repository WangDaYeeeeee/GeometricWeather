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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // Temporary local mirror for artifacts no longer available on JitPack:
        // com.xw.repo:bubbleseekbar:3.20-lite
        maven { url = uri(settings.rootDir.resolve("local-repo")) }
    }
}

rootProject.name = "GeometricWeather"
include(":app")
include(":core")
include(":domain")
include(":data")
include(":presentation")
include(":feature:search")
include(":feature:settings")