package com.tisawesomeness.minecord.command.general;

import java.awt.Color;
import java.text.DecimalFormat;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"info",
			"Shows the bot info",
			null,
			new String[]{"about", "stats"},
			0,
			false,
			false,
			true);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		DiscordUtils.update();
		
		//If the author used the admin keyword and is an elevated user
		boolean elevated = false;
		if (args.length > 0 && args[0].equals("admin") && Database.isElevated(e.getAuthor().getIdLong())) {
			elevated = true;
		}
		
		//Build message
		EmbedBuilder eb = new EmbedBuilder();
		
		eb.setColor(Color.GREEN);
		eb.addField("Author", "@Tis_awesomeness#8617", true);
		eb.addField("Version", Bot.getVersion(), true);
		
		String guilds = Bot.shardManager.getGuilds().size() + "";
		if (Config.getShardCount() > 1) {
			String shards = e.getJDA().getShardInfo().getShardId() + 1 + "/" + Config.getShardCount();
			eb.addField("Shard", shards, true);
			guilds += " {" + e.getJDA().getGuilds().size() + "}";
		}
		eb.addField("Guilds", guilds + "", true);
		
		eb.addField("Uptime", DateUtils.getUptime(), true);
		if (Config.getShowMemory() || elevated) {
			eb.addField("Memory", getMemoryString(), true);
			eb.addField("Boot Time", DateUtils.getBootTime(), true);
		}
		eb.addField("Ping", Bot.shardManager.getAverageGatewayPing() + "ms", true);
		
		eb.addField("Invite", "Use `" + Database.getPrefix(e.getGuild().getIdLong()) + "invite`", true);
		eb.addField("Help Server", Bot.helpServer, true);
		eb.addField("Website", Bot.website, true);
		eb.addField("Credits", "Mojang API, Crafatar, and lucaazalim", true);
		eb.addField("Library", "Java `1.8`, JDA `4.1.1_151`", true);
		
		eb = MessageUtils.addFooter(eb);
		return new Result(Outcome.SUCCESS, eb.build());
	}

	//Calculate memory (taken from stackoverflow)
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	private static final long[] dividers = new long[] {T, G, M, K, 1};
	private static final String[] units = new String[] {"TB", "GB", "MB", "KB", "B"};
	private static String getMemoryString() {
		long value = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if (value < 1) {
			throw new IllegalArgumentException("Invalid file size: " + value);
		}
		String memory = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				memory = format(value, divider, units[i]);
			}
		}
		return memory;
	}

	private static String format(long value, long divider, String unit) {
		double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
	
}
