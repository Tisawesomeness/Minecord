package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermsCommandAdmin extends LegacyCommand {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "permsadmin",
                "Test the bot's permissions in a channel.",
                "<channel id>",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}permsadmin <id>` - Test the bot's permissions for any channel.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}perms 347909541264097281`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You need to specify a channel id.");
        }
        if (!DiscordUtils.isDiscordId(args[0])) {
            return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
        }
        GuildChannel c = Bot.shardManager.getTextChannelById(args[0]);
        if (c == null) {
            return new Result(Outcome.WARNING, ":warning: That channel does not exist.");
        }
        return PermsCommand.run(c);
    }

}
