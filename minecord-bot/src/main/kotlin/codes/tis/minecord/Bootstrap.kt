package codes.tis.minecord

import ch.qos.logback.classic.Logger
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::Args).run {
        if (version) {
            println(BuildProperties.version)
        } else {
            (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).apply { level = logLevel }
            if (!Bot.start(configDir)) {
                exitProcess(1)
            }
        }
    }
}
