// https://docs.gradle.org/current/userguide/settings_file_basics.html

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":minecord-bot")

rootProject.name = "Minecord"