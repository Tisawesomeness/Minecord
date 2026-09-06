plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(libs.kotlinxCoroutines)
}

tasks {
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
