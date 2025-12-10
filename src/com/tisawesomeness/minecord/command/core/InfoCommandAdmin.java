package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommandAdmin extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "infoadmin",
                "Shows the bot info.",
                null,
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}infoadmin` - Shows the bot info, including memory usage and boot time.\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        return InfoCommand.run(true, e.getJDA());
    }

}
