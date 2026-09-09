plugins {
    id("buildsrc.convention.minecord-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.logback)
    implementation(libs.bundles.jda) {
        exclude(module = "opus-java")
        exclude(module = "tink")
    }
    implementation(libs.argparser)
    implementation(libs.bundles.jackson)
    implementation(libs.jackson.yaml)

    testImplementation(libs.bundles.kotest)
}

tasks {
    val minecordVersion = version
    val jdaVersion = libs.versions.jda
    processResources {
        filesMatching("build.properties") {
            expand(
                mapOf(
                    "version" to minecordVersion,
                    "jdaVersion" to jdaVersion,
                ),
            )
        }
    }

    jar {
        manifest {
            attributes["Main-Class"] = "codes.tis.minecord.BootstrapKt"
        }
    }

    shadowJar {
        archiveBaseName = rootProject.name
        archiveClassifier.unset()
        addMultiReleaseAttribute = false
    }
}
