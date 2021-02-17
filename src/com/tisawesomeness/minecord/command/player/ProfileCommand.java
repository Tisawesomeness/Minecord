package com.tisawesomeness.minecord.command.player;

import java.util.ArrayList;
import java.util.Arrays;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

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
    public Result run(String[] args, MessageReceivedEvent e) throws Exception {
		String prefix = MessageUtils.getPrefix(e);
		
		// No arguments message
		if (args.length == 0) {
			String m = ":warning: Incorrect arguments." +
				"\n" + prefix + "profile <username|uuid> [date] " +
				"\n" + DateUtils.dateHelp;
			return new Result(Outcome.WARNING, m, 5);
        }

		// UUID --> Username
		String player = args[0];
		if (player.matches(NameUtils.uuidRegex)) {
			player = NameUtils.getName(player);

			// Check for errors
			if (player == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that UUID exists?";
				return new Result(Outcome.WARNING, m, 1.5);
			} else if (!player.matches(NameUtils.playerRegex)) {
				String m = ":x: The API responded with an error:\n" + player;
				return new Result(Outcome.ERROR, m, 3);
			}
		}

        // Get profile info
        JSONArray payload = new JSONArray();
        payload.put(player);
        String request = RequestUtils.post("https://api.mojang.com/profiles/minecraft", payload.toString());
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		JSONArray profileArr = new JSONArray(request);
		if (profileArr.length() == 0) {
			return new Result(Outcome.WARNING, ":warning: That username does not exist.");
		}
        JSONObject profile = profileArr.getJSONObject(0);
        String uuid = profile.getString("id");
        boolean legacy = profile.optBoolean("legacy");
        boolean demo = profile.optBoolean("demo");

        // Image and NameMC URLs
        String avatarUrl = "https://crafatar.com/avatars/" + uuid;
        String bodyUrl = "https://crafatar.com/renders/body/" + uuid + "?overlay";
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
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
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
            .setColor(Bot.color)
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
        
        return new Result(Outcome.SUCCESS, eb.build());
    }

    private static String boolToString(boolean bool) {
        return bool ? "True" : "False";
    }

}