package com.tisawesomeness.minecord.command.discord;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.TimeUtil;

import java.time.OffsetDateTime;

public class IdCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "id",
                "Gets the creation time of a Discord ID.",
                "<id>",
                new String[]{"snowflake"},
                0,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}id <id>` - Gets the creation time of a Discord ID.\n" +
                "This command does not check if an ID exists.\n" +
                "To get Discord IDs, turn on User Settings > Advanced > Developer Mode, then right click and select \"Copy ID\"\n" +
                "The `{&}user`/`{&}role`/`{&}guild` commands also show IDs.\n" +
                "\n" +
                "Examples:\n" +
                "- `{&}id 211261249386708992`\n" +
                "- `{&}id 292279711034245130`\n";
    }

    @Override
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify an id.");
        } else if (args.length > 1) {
            return new Result(Outcome.WARNING, ":warning: Too many arguments.");
        }
        if (DiscordUtils.isDiscordId(args[0])) {
            OffsetDateTime time = TimeUtil.getTimeCreated(Long.parseLong(args[0]));
            return new Result(Outcome.SUCCESS, "Created " + TimeFormat.RELATIVE.format(time));
        } else {
            return new Result(Outcome.WARNING, ":warning: " + args[0] + " is not a valid id.");
        }
    }

}
