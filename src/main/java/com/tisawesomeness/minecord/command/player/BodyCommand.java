package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class BodyCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "body";
    }

    public Result run(String[] args, CommandContext ctx) {

        //No arguments message
        if (args.length == 0) {
            return ctx.showHelp();
        }

        //Check for overlay argument
        boolean overlay = false;
        int index = MessageUtils.parseBoolean(args, "overlay");
        if (index > 0) {
            overlay = true;
            ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));
            argsList.remove("overlay");
            args = argsList.toArray(new String[argsList.size()]);
        }

        String player = args[0];
        String param = player;
        if (!player.matches(NameUtils.uuidRegex)) {
            String uuid = null;

            //Parse date argument
            if (args.length > 1) {
                long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
                if (timestamp == -1) {
                    return ctx.showHelp();
                }

            //Get the UUID
                uuid = NameUtils.getUUID(player, timestamp);
            } else {
                uuid = NameUtils.getUUID(player);
            }

            //Check for errors
            if (uuid == null) {
                String m = "The Mojang API could not be reached." +
                    "\n" + "Are you sure that username exists?" +
                    "\n" + "Usernames are case-sensitive.";
                return ctx.err(m);
            } else if (!uuid.matches(NameUtils.uuidRegex)) {
                return ctx.err("The API responded with an error:\n" + uuid);
            }

            param = uuid;
        }

        //Fetch body
        String url = "https://crafatar.com/renders/body/" + param.replaceAll("-", "");
        if (overlay) url += "?overlay";
        return ctx.replyRaw(new EmbedBuilder().setImage(url).setColor(Bot.color));
    }

}
