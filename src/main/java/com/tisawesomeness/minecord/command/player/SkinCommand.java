package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.NameUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

public class SkinCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "skin";
    }

    public void run(String[] args, CommandContext ctx) {

        //No arguments message
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }

        ctx.triggerCooldown();
        String player = args[0];
        String param = player;
        if (!player.matches(NameUtils.uuidRegex)) {
            String uuid = null;

            //Parse date argument
            if (args.length > 1) {
                long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
                if (timestamp == -1) {
                    ctx.showHelp();
                    return;
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
                ctx.possibleErr(m);
                return;
            } else if (!uuid.matches(NameUtils.uuidRegex)) {
                ctx.err("The API responded with an error:\n" + uuid);
                return;
            }

            param = uuid;
        }

        //Fetch skin
        String url = "https://crafatar.com/skins/" + param.replaceAll("-", "");
        ctx.replyRaw(new EmbedBuilder().setImage(url).setColor(Bot.color));
    }

}
