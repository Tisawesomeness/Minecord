package com.tisawesomeness.minecord.command.utility;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.awt.Color;

public class StatusCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"status",
			"Checks the status of Mojang servers.",
			null,
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		// Request information from Mojang through Obsidion API
		String request = RequestUtils.get("https://api.obsidion-dev.com/api/v1/mojang/check", null, true);
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be checked.");
		}

		// Extract statuses
		JSONObject status = new JSONObject(request);
		boolean mcStatus = isGreen(status, "https://www.minecraft.net");
		boolean accountStatus = isGreen(status, "https://account.mojang.com");
		boolean authStatus = isGreen(status, "https://authserver.mojang.com");
		boolean textureStatus = isGreen(status, "https://textures.minecraft.net");
		boolean apiStatus = isGreen(status, "https://api.mojang.com");

		boolean allGood = mcStatus && accountStatus && authStatus && textureStatus && apiStatus;
		Color color = allGood ? Color.GREEN : Color.RED;

		// Build message
		String m = "**Minecraft:** " + DiscordUtils.getBoolEmote(mcStatus) +
				"\n" + "**Accounts:** " + DiscordUtils.getBoolEmote(accountStatus) +
				"\n" + "**Auth Server:** " + DiscordUtils.getBoolEmote(authStatus) +
				"\n" + "**Textures:** " + DiscordUtils.getBoolEmote(textureStatus) +
				"\n" + "**Mojang API:** " + DiscordUtils.getBoolEmote(apiStatus);
		
		MessageEmbed me = MessageUtils.embedMessage("Minecraft Status", null, m, color);
		return new Result(Outcome.SUCCESS, me);
	}

	private static boolean isGreen(JSONObject status, String key) {
		return "green".equals(status.optString(key));
	}
	
}
