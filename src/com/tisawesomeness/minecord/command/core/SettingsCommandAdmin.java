package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.LegacyCommand;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class SettingsCommandAdmin extends LegacyCommand {

    @Override
    public CommandInfo getInfo() {
        return new CommandInfo(
                "settingsadmin",
                "Change the bot's settings for another guild.",
                "<guild id> [<setting> <value>]",
                0,
                true,
                true
        );
    }

    @Override
    public String getHelp() {
        return "`{&}settings <guild id>` - View settings for another guild.\n" +
                "`{&}settings <guild id> <setting> <value>` - Changes settings in another guild.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}settings 347765748577468416`\n" +
                "- `{&}settings 347765748577468416 prefix mc!`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        // If the author used the admin keyword and is an elevated user
        String sourcePrefix = MessageUtils.getPrefix(e);
        if (!DiscordUtils.isDiscordId(args[0])) {
            return new Result(Outcome.WARNING, ":warning: Not a valid ID!");
        }
        if (Bot.shardManager.getGuildById(args[0]) == null) {
            return new Result(Outcome.WARNING, ":warning: Minecord does not know that guild ID!");
        }
        long gid = Long.parseLong(args[0]);
        args = Arrays.copyOfRange(args, 1, args.length);
        String targetPrefix = Database.getPrefix(gid);
        return SettingsCommand.run(args, e, sourcePrefix, targetPrefix, gid, true);
    }

}
