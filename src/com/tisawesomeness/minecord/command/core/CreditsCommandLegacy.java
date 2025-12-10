package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreditsCommandLegacy extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "credits",
                "See who made the bot possible.",
                null,
                0,
                true,
                false
        );
    }
    @Override
    public String[] getAliases() {
        return CreditsCommand.legacyAliases();
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) {
        return CreditsCommand.run(e.getAuthor());
    }

}
