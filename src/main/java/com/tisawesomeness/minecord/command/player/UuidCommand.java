package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import lombok.NonNull;

import java.util.Arrays;

public class UuidCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "uuid";
    }

    public Result run(String[] args, CommandContext ctx) {

        // No arguments message
        if (args.length == 0) {
            return ctx.showHelp();
        }

        String username = args[0];
        String uuid = null;

        // Parse date argument
        if (args.length > 1) {
            long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
            if (timestamp == -1) {
                return new Result(Outcome.WARNING, MessageUtils.dateErrorString(ctx.prefix, "uuid"));
            }

        // Get the UUID
            uuid = NameUtils.getUUID(username, timestamp);
        } else {
            uuid = NameUtils.getUUID(username);
        }

        // Check for errors
        if (uuid == null) {
            String m = ":x: The Mojang API could not be reached." +
                "\n" + "Are you sure that username exists?" +
                "\n" + "Usernames are case-sensitive.";
            return new Result(Outcome.WARNING, m);
        } else if (!uuid.matches(NameUtils.uuidRegex)) {
            String m = ":x: The API responded with an error:\n" + uuid;
            return new Result(Outcome.ERROR, m);
        }

        // Get NameMC url
        String url = "https://namemc.com/profile/" + uuid;

        // Proper apostrophe grammar
        String title = username;
        if (title.endsWith("s")) {
            title = title + "' UUID";
        } else {
            title = title + "'s UUID";
        }

        String m = String.format("Short: `%s`\nLong: `%s`", uuid, NameUtils.formatUUID(uuid));

        return new Result(Outcome.SUCCESS, ctx.embedURL(title, url, m).build());
    }

}
