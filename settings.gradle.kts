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
    plugins {
        id("org.greenrobot.greendao") version "3.3.1"
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.greenrobot.greendao") {
                useModule("org.greenrobot:greendao-gradle-plugin:${requested.version}")
            }
        }
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