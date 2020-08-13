package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class HistoryCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "history";
    }

    public Result run(String[] args, CommandContext ctx) {

        // No arguments message
        if (args.length == 0) {
            return ctx.showHelp();
        }

        ctx.triggerCooldown();
        String player = args[0];
        if (!player.matches(NameUtils.uuidRegex)) {
            String uuid = null;

            // Parse date argument
            if (args.length > 1) {
                long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
                if (timestamp == -1) {
                    return ctx.showHelp();
                }

            // Get the UUID
                uuid = NameUtils.getUUID(player, timestamp);
            } else {
                uuid = NameUtils.getUUID(player);
            }

            // Check for errors
            if (uuid == null) {
                String m = "The Mojang API could not be reached." +
                    "\n" + "Are you sure that username exists?" +
                    "\n" + "Usernames are case-sensitive.";
                return ctx.possibleErr(m);
            } else if (!uuid.matches(NameUtils.uuidRegex)) {
                return ctx.err("The API responded with an error:\n" + uuid);
            }

            player = uuid;
        }

        // Fetch name history
        String url = "https://api.mojang.com/user/profiles/" + player.replaceAll("-", "") + "/names";
        String request = RequestUtils.get(url);
        if (request == null) {
            return ctx.err("The Mojang API could not be reached.");
        }

        // Loop over each name change
        JSONArray names = new JSONArray(request);
        ArrayList<String> lines = new ArrayList<String>();
        for (int i = 0; i < names.length(); i++) {

            // Get info
            JSONObject change = names.getJSONObject(i);
            String name = change.getString("name");
            String date;
            if (change.has("changedToAt")) {
                date = DateUtils.getDateAgo(change.getLong("changedToAt"));
            } else {
                date = "Original";
            }

            // Add to lines in reverse
            lines.add(0, String.format("**%d.** `%s` | %s", i + 1, name, date));
        }

        // Get NameMC url
        player = names.getJSONObject(names.length() - 1).getString("name");
        url = "https://namemc.com/profile/" + player;

        // Proper apostrophe grammar
        if (player.endsWith("s")) {
            player += "' Name History";
        } else {
            player += "'s Name History";
        }

        EmbedBuilder eb = new EmbedBuilder()
            .setTitle(player, url);

        // Truncate until 6000 char limit reached
        int chars = MessageUtils.getTotalChars(lines);
        boolean truncated = false;
        while (chars > 6000 - 4) {
            truncated = true;
            lines.remove(lines.size() - 1);
            chars = MessageUtils.getTotalChars(lines);
        }
        if (truncated) {
            lines.add("...");
        }
        // If over 2048, use fields, otherwise use description
        if (chars > 2048) {
            // Split into fields, avoiding 1024 field char limit
            for (String field : MessageUtils.splitLinesByLength(lines, 1024)) {
                eb.addField("Name History", field, false);
            }
        } else {
            eb.setDescription(String.join("\n", lines));
        }

        return ctx.reply(eb);
    }

}
