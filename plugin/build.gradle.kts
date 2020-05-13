import org.jetbrains.intellij.tasks.PatchPluginXmlTask

plugins {
    kotlin("jvm")
    id("org.jetbrains.intellij")
}

intellij {
    version = "2020.1.1"
}

tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        Initial version.
    """.trimIndent())
}
