pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
        maven(uri("https://jitpack.io"))
        maven(uri("https://dl.bintray.com/spark/maven"))
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(uri("https://jitpack.io"))
        maven(uri("https://dl.bintray.com/spark/maven"))
    }
}

rootProject.name = "MaN"
include(":app")
