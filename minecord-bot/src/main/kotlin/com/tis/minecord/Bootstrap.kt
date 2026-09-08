package com.tis.minecord

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.tis.minecord.util.env
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import dev.minn.jda.ktx.util.SLF4J
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

val log by SLF4J

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::Args).run {
        if (version) {
            println(BuildProperties.version)
        } else {
            (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).apply { level = Level.INFO }

            val token = Token.make(env("MINECORD_TOKEN"))
            if (token == null) {
                log.error("Token env var not set")
                exitProcess(1)
            }

            if (!Bot.start(token)) {
                exitProcess(1)
            }
        }
    }
}

private class Args(parser: ArgParser) {
    val version by parser.flagging(
        "-v", "--version",
        help = "show version and exit")
}
