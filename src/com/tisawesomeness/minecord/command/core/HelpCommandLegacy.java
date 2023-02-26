package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.command.LegacyCommand;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCommandLegacy extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "help",
                "Displays help for the bot, a command, or a module.",
                "[<command>|<module>|extra]",
                0,
                true,
                false
        );
    }
    @Override
    public String[] getAliases() {
        return HelpCommand.legacyAliases();
    }
    @Override
    public String getHelp() {
        return HelpCommand.help;
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        String page = args.length == 0 ? null : String.join(" ", args);
        return HelpCommand.run(page, e.getAuthor(), e.getJDA());
    }

}
