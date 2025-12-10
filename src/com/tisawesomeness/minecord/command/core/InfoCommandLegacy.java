package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommandLegacy extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "info",
                "Shows the bot info.",
                null,
                0,
                true,
                false
        );
    }
    @Override
    public String[] getAliases() {
        return InfoCommand.legacyAliases();
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) {
        return InfoCommand.run(false, e.getJDA());
    }

}
