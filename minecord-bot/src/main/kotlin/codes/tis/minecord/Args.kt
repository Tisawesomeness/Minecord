package codes.tis.minecord

import ch.qos.logback.classic.Level
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.nio.file.Path

class Args(parser: ArgParser) {
    val version by parser.flagging(
        "-v", "--version",
        help = "show version and exit",
    )

    val configDir: Path by parser
        .storing(
            "-c", "--config",
            help = "config directory path",
        ) { Path.of(this) }
        .default(Path.of("minecord"))

    val logLevel: Level by parser
        .storing(
            "--log-level",
            help = "set the log level",
        ) { parseLevel(this) ?: throw IllegalArgumentException("Invalid level $this") }
        .default(Level.INFO)
}

@Suppress("DEPRECATION") // Using deprecated ALL as indicator for invalid input
private fun parseLevel(name: String): Level? {
    return Level.toLevel(name, Level.ALL).takeUnless { it == Level.ALL }
}
