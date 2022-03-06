package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class CapeCommand extends Command {

	public CommandInfo getInfo() {
		return new CommandInfo(
			"cape",
			"Gets the cape of a player.",
			"<username|uuid>",
			null,
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}cape <player>` - Gets an image of the player's cape.\n" +
			"Includes Minecraft, Optifine, LabyMod and MinecraftCapes.co.uk capes.\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"\n" +
			"Examples:\n" +
			"`{&}cape jeb_`\n" +
			"`{&}cape 853c80ef3c3749fdaa49938b674adae6`\n" +
			"`{&}cape 069a79f4-44e9-4726-a5be-fca90e38aaf5`\n";
	}

	public Result run(String[] args, MessageReceivedEvent e) {
		// No arguments message
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.");
		}

		// Get playername
		String player = args[0];
		String uuid = player;
		if (NameUtils.isUuid(player)) {
			try {
				player = NameUtils.getName(player);
				if (player == null) {
					return new Result(Outcome.SUCCESS, "There is no player with that UUID.");
				} else if (!NameUtils.isUsername(player)) {
					String m = ":x: The API responded with an error:\n" + player;
					return new Result(Outcome.ERROR, m);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				return new Result(Outcome.ERROR, "The Mojang API could not be reached.");
			}

		} else {
			if (!NameUtils.isUsername(player)) {
				return new Result(Outcome.WARNING, ":warning: That username is invalid.");
			}

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

			uuid = uuid.replace("-", "").toLowerCase();
		}

		// Minecraft capes
		MessageChannel c = e.getChannel();
		boolean hasCape = false;
		String url = "https://crafatar.com/capes/" + uuid;
		if (RequestUtils.checkURL(url)) {
			sendImage(c, "Minecraft Cape", url);
			hasCape = true;
		}
		// Optifine cape
		String optifineUrl = String.format("http://s.optifine.net/capes/%s.png", player);
		if (RequestUtils.checkURL(optifineUrl)) {
			sendImage(c, "Optifine Cape", optifineUrl);
			hasCape = true;
		}
		
		if (!hasCape) return new Result(Outcome.WARNING, ":warning: " + player + " does not have a cape!");
		return new Result(Outcome.SUCCESS);
	}

	private static void sendImage(MessageChannel c, String title, String url) {
		c.sendMessageEmbeds(new EmbedBuilder().setTitle(title).setColor(Bot.color).setImage(url).build()).queue();
	}
	
}
