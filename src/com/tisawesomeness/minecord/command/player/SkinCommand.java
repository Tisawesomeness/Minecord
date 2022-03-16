package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class SkinCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "skin",
                "Gets the skin of a player.",
                "<username|uuid>",
                null,
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}skin <player> [overlay?]` - Gets an image of the player's skin.\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "\n" +
                "Examples:\n" +
                "`{&}skin Tis_awesomeness`\n" +
                "`{&}skin jeb_`\n" +
                "`{&}skin f6489b797a9f49e2980e265a05dbc3af`\n" +
                "`{&}skin 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
    }

    public Result run(String[] args, MessageReceivedEvent e) {
        //No arguments message
        if (args.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }

        String player = args[0];
        String param = player;
        if (!NameUtils.isUuid(player)) {
            if (!NameUtils.isUsername(player)) {
                return new Result(Outcome.WARNING, ":warning: That username is invalid.");
            }

            String uuid;
            try {
                uuid = NameUtils.getUUID(player);
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

            param = uuid;
        }

        //Fetch skin
        String url = "https://crafatar.com/skins/" + param.replace("-", "");
        return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
    }

}
