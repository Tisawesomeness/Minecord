package codes.tis.minecord.testutil

import codes.tis.minecord.command.CommandContext

class TestContext : CommandContext {
    val replies = mutableListOf<String>()

    override fun reply(message: String) {
        replies.add(message)
    }
}
