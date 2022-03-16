package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class UuidCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "uuid",
                "Gets the UUID of a player.",
                "<username>",
                new String[]{"u"},
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}uuid <player>` - Gets a player's short and long UUID.\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "\n" +
                "Examples:\n" +
                "`{&}uuid Tis_awesomeness`\n" +
                "`{&}uuid jeb_`\n" +
                "`{&}uuid f6489b797a9f49e2980e265a05dbc3af`\n" +
                "`{&}uuid 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        // No arguments message
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }

        String username = args[0];
        if (!NameUtils.isUsername(username)) {
            return new Result(Outcome.WARNING, ":warning: That username is invalid.");
        }

        String uuid;
        try {
            uuid = NameUtils.getUUID(username);
            if (uuid == null) {
                return new Result(Outcome.SUCCESS, "That username does not exist.");
            } else if (!NameUtils.isUuid(uuid)) {
                String m = ":x: The API responded with an error:\n" + uuid;
                return new Result(Outcome.ERROR, m);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return new Result(Outcome.ERROR, "The Mojang API could not be reached.");
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
        MessageEmbed me = MessageUtils.embedMessage(title, url, m, Bot.color);

        return new Result(Outcome.SUCCESS, new EmbedBuilder(me).build());
    }

}
