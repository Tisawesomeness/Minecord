package com.tisawesomeness.minecord.command.player;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.NameUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.util.Arrays;

public class CapeCommand extends AbstractPlayerCommand {

	public @NonNull String getId() {
		return "cape";
	}
	public CommandInfo getInfo() {
		return new CommandInfo(
                false,
				false,
				true
		);
	}

	public Result run(CommandContext ctx) {

		// No arguments message
		if (ctx.args.length == 0) {
			return ctx.showHelp();
		}

		// Get playername
		String player = ctx.args[0];
		String uuid = player;
		if (player.matches(NameUtils.uuidRegex)) {
			player = NameUtils.getName(player);

			// Check for errors
			if (player == null) {
				String m = ":x: The Mojang API could not be reached." +
					"\n" + "Are you sure that UUID exists?";
				return new Result(Outcome.WARNING, m);
			} else if (!player.matches(NameUtils.playerRegex)) {
				String m = ":x: The API responded with an error:\n" + player;
				return new Result(Outcome.ERROR, m);
			}
		} else {
			// Parse date argument
			if (ctx.args.length > 1) {
				long timestamp = DateUtils.getTimestamp(Arrays.copyOfRange(ctx.args, 1, ctx.args.length));
				if (timestamp == -1) {
					return new Result(Outcome.WARNING, MessageUtils.dateErrorString(ctx.prefix, "skin"));
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
				return new Result(Outcome.WARNING, m);
			} else if (!uuid.matches(NameUtils.uuidRegex)) {
				String m = ":x: The API responded with an error:\n" + uuid;
				return new Result(Outcome.ERROR, m);
			}

			uuid = uuid.replace("-", "").toLowerCase();
		}

		// Minecraft capes
		MessageChannel c = ctx.e.getChannel();
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
