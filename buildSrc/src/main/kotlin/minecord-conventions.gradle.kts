package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

plugins {
    kotlin("jvm")
}

group = "com.tis.minecord"
version = project.extra["minecord_version"] as String

kotlin {
    jvmToolchain(25)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
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
