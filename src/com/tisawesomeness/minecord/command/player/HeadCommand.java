package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.ArrayUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class HeadCommand extends Command {

    public CommandInfo getInfo() {
        return new CommandInfo(
                "head",
                "Gets the head of a player.",
                "<username|uuid> [<overlay?>]",
                null,
                2000,
                false,
                false,
                true
        );
    }

    public String getHelp() {
        return "`{&}avatar <player> [<overlay?>]` - Gets an image of the player's head.\n" +
                "- `<player>` can be a username or a UUID.\n" +
                "- `[<overlay?>]` true or false, whether to include the second skin layer, default true.\n" +
                "\n" +
                "Examples:\n" +
                "`{&}head Tis_awesomeness`\n" +
                "`{&}head jeb_`\n" +
                "`{&}head f6489b797a9f49e2980e265a05dbc3af`\n" +
                "`{&}head 069a79f4-44e9-4726-a5be-fca90e38aaf5 overlay`\n";
    }

    public Result run(String[] argsOrig, MessageReceivedEvent e) {
        //No arguments message
        if (argsOrig.length == 0) {
            return new Result(Outcome.WARNING, ":warning: You must specify a player.");
        }
        String[] args = argsOrig;

        //Check for overlay argument
        boolean overlay = false;
        int index = MessageUtils.parseBoolean(args, "overlay");
        if (index != -1) {
            overlay = true;
            args = ArrayUtils.remove(args, index);
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

        //Fetch head
        String url = "https://crafatar.com/renders/head/" + param.replace("-", "") + ".png";
        if (overlay) url += "?overlay";
        return new Result(Outcome.SUCCESS, new EmbedBuilder().setImage(url).setColor(Bot.color).build());
    }

}
