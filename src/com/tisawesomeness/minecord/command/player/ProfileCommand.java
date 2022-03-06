package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"profile",
			"Get info on a Minecraft account.",
			"<username|uuid>",
			new String[]{"p", "player"},
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
        return "`{&}profile <player>` - Get info on a Minecraft account.\n" +
            "Includes username, UUIDs, name history, skin, cape, and avatar.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}profile Tis_awesomeness`\n" +
			"`{&}profile jeb_`\n" +
			"`{&}profile f6489b797a9f49e2980e265a05dbc3af`\n" +
			"`{&}profile 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
	}

    @Override
    public Result run(String[] args, MessageReceivedEvent e) {

		// No arguments message
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
		}

		// Username --> UUID
		String player = args[0];
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

			player = uuid;
		}

        // Get profile info
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + player.replace("-", "");
		String request;
		try {
			request = RequestUtils.get(url);
		} catch (IOException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		JSONObject profile = new JSONObject(request);
        String name = profile.getString("name");
        boolean legacy = profile.optBoolean("legacy");
        boolean demo = profile.optBoolean("demo");

        // Image and NameMC URLs
        String avatarUrl = "https://crafatar.com/avatars/" + player;
        String bodyUrl = "https://crafatar.com/renders/body/" + player + "?overlay";
        String skinUrl = "https://crafatar.com/skins/" + player;
        String nameUrl = "https://namemc.com/profile/" + name;

        // Get cape
		String cape;
		String capeUrl = "https://crafatar.com/capes/" + player;
		if (RequestUtils.checkURL(capeUrl)) {
			cape = MarkdownUtil.maskedLink("Open Image", capeUrl);
		} else {
			cape = "No cape.";
		}

		// Fetch name history
		String historyUrl = "https://api.mojang.com/user/profiles/" + player + "/names";
		try {
			request = RequestUtils.get(historyUrl);
		} catch (IOException ex) {
			ex.printStackTrace();
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		
		// Loop over each name change
		JSONArray names = new JSONArray(request);
		ArrayList<String> lines = new ArrayList<>();
		for (int i = 0; i < names.length(); i++) {
			
			// Get info
			JSONObject change = names.getJSONObject(i);
			String nameChange = change.getString("name");
			String date;
			if (change.has("changedToAt")) {
				date = TimeFormat.RELATIVE.format(change.getLong("changedToAt"));
			} else {
				date = "Original";
			}
			
			// Add to lines in reverse
			lines.add(0, String.format("**%d.** `%s` | %s", i + 1, nameChange, date));
		}

        EmbedBuilder eb = new EmbedBuilder()
            .setAuthor("Profile for " + name, nameUrl, avatarUrl)
            .setColor(Bot.color)
            .setThumbnail(bodyUrl)
            .setDescription(String.format("Short UUID: `%s`\nLong UUID: `%s`", player, NameUtils.formatUUID(player)))
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
        
        return new Result(Outcome.SUCCESS, eb.build());
    }

    private static String boolToString(boolean bool) {
        return bool ? "True" : "False";
    }

}
