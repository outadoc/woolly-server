plugins {
    application
    kotlin("jvm") version "1.5.0"
}

group = "fr.woolly.auth"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://nexus.outadoc.fr/repository/public") }
}

dependencies {
    implementation(libs.mastodonk)

    implementation(libs.ktor.cio)
    implementation(libs.ktor.core)
    implementation(libs.ktor.locations)
    implementation(libs.ktor.serialization.kotlinx)
    implementation(libs.logback)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.mysql.connector)

    testImplementation(libs.ktor.tests)
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
