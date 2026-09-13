package buildsrc.convention

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

plugins {
    kotlin("jvm")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

group = "codes.tis.minecord"
version = libs.findVersion("minecord").get().requiredVersion

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Reproducible builds
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true

    eachFile {
        permissions {
            val isExec = Files.getPosixFilePermissions(file.toPath()).contains(PosixFilePermission.OWNER_EXECUTE)
            unix(if (isExec) "755" else "644")
        }
    }
    dirPermissions { unix("755") }
}
