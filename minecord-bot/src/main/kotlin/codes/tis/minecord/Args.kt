package codes.tis.minecord

import ch.qos.logback.classic.Level
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Args(parser: ArgParser) {
    val version by parser.flagging(
        "-v", "--version",
        help = "show version and exit",
    )
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
