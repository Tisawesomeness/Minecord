plugins {
    id("buildsrc.convention.minecord-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotest)
}

dependencies {
    implementation(libs.kotlinxCoroutines)
    implementation(libs.logback)
    implementation(libs.bundles.jda) {
         exclude(module="opus-java")
         exclude(module="tink")
    }
    implementation(libs.argparser)

    testImplementation(libs.bundles.kotest)
}

tasks {
    val minecordVersion = version
    val jdaVersion = libs.versions.jda
    processResources {
        filesMatching("build.properties") {
            expand(mapOf(
                "version" to minecordVersion,
                "jdaVersion" to jdaVersion
            ))
        }
    }

    jar {
        manifest {
            attributes["Main-Class"] = "com.tis.minecord.BootstrapKt"
        }
    }

    shadowJar {
        archiveBaseName = rootProject.name
        archiveClassifier.unset()
        addMultiReleaseAttribute = false
    }
}
