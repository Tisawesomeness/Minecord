package com.tisawesomeness.minecord.command.general;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"info",
			"Shows the bot info",
			null,
			new String[]{
				"about",
				"stats"},
			0,
			false,
			false,
			true);
	}
	
	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	public static final String helpServer = "https://discord.gg/7keuKSb";
	
	public Result run(String[] args, MessageReceivedEvent e) {
		Config.update();
		
		//If the author used the admin keyword and is an elevated user
		boolean elevated = false;
		if (args.length > 0 && args[0].equals("admin") && Config.getElevatedUsers().contains(e.getAuthor().getId())) {
			elevated = true;
		}
		
		//Calculate memory (taken from stackoverflow)
		long value = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		final long[] dividers = new long[] {T, G, M, K, 1};
		final String[] units = new String[] {"TB", "GB", "MB", "KB", "B"};
		if (value < 1) {
			throw new IllegalArgumentException("Invalid file size: " + value);
		}
		String memory = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				memory = format(value, divider, units[i]);
				break;
			}
		}
		
		//Build message
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.addField("Author", "@Tis_awesomeness#8617", true);
		eb.addField("Version", Bot.getVersion(), true);
		eb.addField("Guilds", e.getJDA().getGuilds().size() + "", true);
		eb.addField("Channels", e.getJDA().getTextChannels().size() + "", true);
		eb.addField("Users", e.getJDA().getUsers().size() + "", true);
		ArrayList<User> users = new ArrayList<User>(e.getJDA().getUsers());
		for (User u : new ArrayList<User>(users)) {
			if (u.isBot() || u.isFake()) {
				users.remove(u);
			}
		}
		eb.addField("Humans", users.size() + "", true);
		eb.addField("Uptime", DateUtils.getUptime(), true);
		if (Config.getShowMemory() || elevated) {
			eb.addField("Memory", memory, true);
		}
		eb.addField("Help Server", helpServer, true);
		eb.addField("Credits", "Mojang API, Crafatar, and MCAPI", true);
		eb.addField("Library", "Java `1.8.0_101`, JDA `3.0.BETA2_135`", true);
		
		eb = MessageUtils.addFooter(eb);
		return new Result(Outcome.SUCCESS, eb.build());
	}

	private static String format(long value, long divider, String unit) {
		double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
	
}
