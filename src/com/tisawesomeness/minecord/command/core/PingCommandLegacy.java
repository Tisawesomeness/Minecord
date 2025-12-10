package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingCommandLegacy extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "ping",
                "Pings the bot.",
                null,
                0,
                true,
                false
        );
    }
    @Override
    public String getHelp() {
        return PingCommand.help;
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        return PingCommand.run();
    }

}
