package com.tisawesomeness.minecord.command.player;

import java.io.IOException;
import java.util.Arrays;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CapeCommand extends Command {

	public CommandInfo getInfo() {
		return new CommandInfo(
			"cape",
			"Gets the cape of a player.",
			"<username|uuid> [date]",
			null,
			2000,
			false,
			false,
			true
		);
	}

	public String getHelp() {
		return "`{&}cape <player> [date]` - Gets an image of the player's cape.\n" +
			"Includes Minecraft, Optifine, LabyMod and MinecraftCapes.co.uk capes.\n" +
			"\n" +
			"- `<player>` can be a username or a UUID.\n" +
			"- " + DateUtils.dateHelp + "\n" +
			"\n" +
			"Examples:\n" +
			"`{&}cape jeb_`\n" +
			"`{&}cape Notch 3/2/06 2:47:32`\n" +
			"`{&}cape 853c80ef3c3749fdaa49938b674adae6`\n" +
			"`{&}cape 069a79f4-44e9-4726-a5be-fca90e38aaf5 3/26`\n";
	}

	public Result run(String[] args, MessageReceivedEvent e) {
		String prefix = MessageUtils.getPrefix(e);

		// No arguments message
		if (args.length == 0) {
			return new Result(Outcome.WARNING, ":warning: You must specify a player.", 5);
		}

		// Get playername
		String player = args[0];
		String uuid = player;
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
		} else {
			// Parse date argument
			if (args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(args, 1, args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(prefix, "skin"));
				}

				// Get the UUID
				uuid = NameUtils.getUUID(player, timestamp);
			} else {
				uuid = NameUtils.getUUID(player);
			}

			// Check for errors
			if (uuid == null) {
				String m = ":x: The Mojang API could not be reached." +
						"\n" +"Are you sure that username exists?" +
						"\n" + "Usernames are case-sensitive.";
				return new Result(Outcome.WARNING, m, 2);
			} else if (!uuid.matches(NameUtils.uuidRegex)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m, 3);
			}

			uuid = uuid.replace("-", "").toLowerCase();
		}

		// Minecraft capes
		MessageChannel c = e.getChannel();
		boolean hasCape = false;
		if (NameUtils.mojangUUIDs.contains(uuid)) {
			// Mojang cape
			sendImage(c, "Minecraft Cape", "https://minecord.github.io/capes/mojang.png");
			hasCape = true;
		} else {
			// Other minecraft capes
			String url = "https://crafatar.com/capes/" + uuid;
			if (RequestUtils.checkURL(url)) {
				sendImage(c, "Minecraft Cape", url);
				hasCape = true;
			}
		}
		// Optifine cape
		String url = String.format("http://s.optifine.net/capes/%s.png", player);
		if (RequestUtils.checkURL(url)) {
			sendImage(c, "Optifine Cape", url);
			hasCape = true;
		}
		// LabyMod cape (doesn't show in embed, download required)
		url = String.format("http://capes.labymod.net/capes/%s", NameUtils.formatUUID(uuid));
		if (RequestUtils.checkURL(url)) {
			MessageEmbed emb = new EmbedBuilder().setTitle("LabyMod Cape").setColor(Bot.color).setImage("attachment://cape.png").build();
			try {
				c.sendFile(RequestUtils.downloadImage(url), "cape.png").embed(emb).queue();
				hasCape = true;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		// MinecraftCapes.co.uk
		url = String.format("https://www.minecraftcapes.co.uk/gallery/grab-player-capes/%s", player);
		if (RequestUtils.checkURL(url, true)) {
			sendImage(c, "MinecraftCapes.co.uk Cape", url);
			hasCape = true;
		}
		
		if (!hasCape) return new Result(Outcome.WARNING, ":warning: " + player + " does not have a cape!");
		return new Result(Outcome.SUCCESS);
	}

	private static void sendImage(MessageChannel c, String title, String url) {
		c.sendMessage(new EmbedBuilder().setTitle(title).setColor(Bot.color).setImage(url).build()).queue();
	}
	
}
