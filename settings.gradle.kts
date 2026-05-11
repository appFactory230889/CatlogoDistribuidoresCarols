pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io") // 👈 necesario para ucrop
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // 👈 necesario para ucrop
    }
}

rootProject.name = "Catálogo Distribuidores Carol´s"
include(":app")
 