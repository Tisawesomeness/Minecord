package codes.tis.minecord

import java.util.Properties

object BuildProperties {
    val version: String
    val jdaVersion: String

    init {
        val props = Properties()
        BuildProperties::class.java.getResourceAsStream("/build.properties")?.use {
            props.load(it)
        } ?: error("build.properties not found on classpath")
        version = props.getProperty("version")
        jdaVersion = props.getProperty("jdaVersion")
    }
}
