plugins {
    kotlin("jvm")
}

val kotestVersion: String by project
val kotlinVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    implementation("com.github.mpetazzoni:ttorrent:ttorrent-2.0")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.0.5")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.0.5")
    testImplementation("io.kotest:kotest-property-jvm:4.0.5")
}
