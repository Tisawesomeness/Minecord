package codes.tis.minecord.command.impl

import codes.tis.minecord.command.tree.command
import codes.tis.minecord.command.tree.genericCommand

val PingCommand = genericCommand("ping").topLevel {
    command { ctx -> ctx.reply("pong") }
}
