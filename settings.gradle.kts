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

rootProject.name = "Ritm"

include(
    ":app",
    ":core:mvi",
    ":core:model",
    ":core:database",
    ":core:data",
    ":core:designsystem",
    ":core:ui",
    ":core:navigation",
    ":feature:splash",
    ":feature:today",
    ":feature:statistics",
)
