package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileCommand extends AbstractPlayerCommand {

    public @NonNull String getId() {
        return "profile";
    }

    public void run(String[] args, CommandContext ctx) {

        // No arguments message
        if (args.length == 0) {
            ctx.showHelp();
            return;
        }

        // UUID --> Username
        ctx.triggerCooldown();
        String player = args[0];
        if (player.matches(NameUtils.uuidRegex)) {
            player = NameUtils.getName(player);

            // Check for errors
            if (player == null) {
                String m = "The Mojang API could not be reached." +
                    "\n" + "Are you sure that UUID exists?";
                ctx.possibleErr(m);
                return;
            } else if (!player.matches(NameUtils.playerRegex)) {
                ctx.err("The API responded with an error:\n" + player);
                return;
            }
        
        // Username + Date --> UUID --> Username
        } else if (args.length > 1) {
            // Parse date argument
            long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
            if (timestamp == -1) {
                ctx.showHelp();
                return;
            }

            // Get the UUID
            String uuid = NameUtils.getUUID(player, timestamp);

            // Check for errors
            if (uuid == null) {
                String m = ":x: The Mojang API could not be reached." +
                        "\n" +"Are you sure that username exists?" +
                        "\n" + "Usernames are case-sensitive.";
                ctx.possibleErr(m);
                return;
            } else if (!uuid.matches(NameUtils.uuidRegex)) {
                ctx.err("The API responded with an error:\n" + uuid);
                return;
            }

            uuid = uuid.replace("-", "").toLowerCase();
            player = NameUtils.getName(uuid);

            // Check for errors
            if (player == null) {
                String m = ":x: The Mojang API could not be reached." +
                        "\n" +"Are you sure that username exists?" +
                        "\n" + "Usernames are case-sensitive.";
                ctx.possibleErr(m);
                return;
            } else if (!player.matches(NameUtils.playerRegex)) {
                ctx.err("The API responded with an error:\n" + player);
                return;
            }
        }

        // Get profile info
        JSONArray payload = new JSONArray();
        payload.put(player);
        String request = RequestUtils.post("https://api.mojang.com/profiles/minecraft", payload.toString());
        if (request == null) {
            ctx.err("The Mojang API could not be reached.");
            return;
        }
        JSONObject profile = new JSONArray(request).getJSONObject(0);
        String uuid = profile.getString("id");
        boolean legacy = profile.optBoolean("legacy");
        boolean demo = profile.optBoolean("demo");

        // Image and NameMC URLs
        String avatarUrl = "https://crafatar.com/avatars/" + uuid;
        String bodyUrl = "https://crafatar.com/renders/body/" + uuid;
        String skinUrl = "https://crafatar.com/skins/" + uuid;
        String nameUrl = "https://namemc.com/profile/" + player;

        // Get cape
        String cape;
        if (NameUtils.mojangUUIDs.contains(uuid)) {
            // Mojang cape
            cape = MarkdownUtil.maskedLink("Open Image", "https://minecord.github.io/capes/mojang.png");
        } else {
            // Other minecraft capes
            String url = "https://crafatar.com/capes/" + uuid;
            if (RequestUtils.checkURL(url)) {
                cape = MarkdownUtil.maskedLink("Open Image", url);
            } else {
                cape = "No cape.";
            }
        }

        // Fetch name history
        String historyUrl = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        request = RequestUtils.get(historyUrl);
        if (request == null) {
            ctx.err("The Mojang API could not be reached.");
            return;
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
                date = DateUtils.getDateAgoShort(change.getLong("changedToAt"));
            } else {
                date = "Original";
            }

            // Add to lines in reverse
            lines.add(0, String.format("**%d.** `%s` | %s", i + 1, name, date));
        }

        EmbedBuilder eb = new EmbedBuilder()
            .setAuthor("Profile for " + player, nameUrl, avatarUrl)
            .setThumbnail(bodyUrl)
            .setDescription(String.format("Short UUID: `%s`\nLong UUID: `%s`", uuid, NameUtils.formatUUID(uuid)))
            .addField("Skin", MarkdownUtil.maskedLink("Open Image", skinUrl), true)
            .addField("Cape", cape, true)
            .addField("Account Info", String.format("Legacy: `%s`\nDemo: `%s`", boolToString(legacy), boolToString(demo)), true);

        // Truncate until 6000 char limit reached
        int chars = MessageUtils.getTotalChars(lines);
        boolean truncated = false;
        while (chars > 6000 - 4 - eb.length()) {
            truncated = true;
            lines.remove(lines.size() - 1);
            chars = MessageUtils.getTotalChars(lines);
        }
        if (truncated) {
            lines.add("...");
        }
        // Split into fields, avoiding 1024 field char limit
        for (String field : MessageUtils.splitLinesByLength(lines, 1024)) {
            eb.addField("Name History", field, false);
        }

        ctx.reply(eb);
    }

    private static String boolToString(boolean bool) {
        return bool ? "True" : "False";
    }

}