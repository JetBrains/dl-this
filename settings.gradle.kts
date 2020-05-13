pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        kotlin("jvm") version kotlinVersion apply false
    }
}

rootProject.name = "dl-this"

include(":backend")
