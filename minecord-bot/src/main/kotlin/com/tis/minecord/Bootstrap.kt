package com.tis.minecord

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::Args).run {
        if (version) {
            println(BuildProperties.version)
        } else {
            println("run")
        }
    }
}

private class Args(parser: ArgParser) {
    val version by parser.flagging(
        "-v", "--version",
        help = "show version and exit")
}
