plugins {
    id("buildsrc.convention.minecord-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotest)
}

dependencies {
    implementation(libs.kotlinxCoroutines)
    implementation(libs.argparser)

    testImplementation(libs.bundles.kotest)
}

tasks {
    val versionValue = version
    processResources {
        filesMatching("build.properties") {
            expand(mapOf("version" to versionValue))
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
