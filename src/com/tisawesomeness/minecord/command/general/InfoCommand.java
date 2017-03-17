package com.tisawesomeness.minecord.command.general;

import java.text.DecimalFormat;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.core.EmbedBuilder;
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
	private static final String helpServer = "https://discord.gg/tPf5Mka";
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Calculate uptime
		long uptimeRaw = System.currentTimeMillis() - Bot.birth;
		uptimeRaw = Math.floorDiv(uptimeRaw, 1000);
		String uptime = "";
		
		if (uptimeRaw >= 86400) {
			long days = Math.floorDiv(uptimeRaw, 86400);
			uptime = days + "d";
			uptimeRaw = uptimeRaw - days * 86400;
		}
		if (uptimeRaw >= 3600) {
			long hours = Math.floorDiv(uptimeRaw, 3600);
			uptime = uptime + hours + "h";
			uptimeRaw = uptimeRaw - hours * 3600;
		}
		if (uptimeRaw >= 60) {
			long minutes = Math.floorDiv(uptimeRaw, 60);
			uptime = uptime + minutes + "m";
			uptimeRaw = uptimeRaw - minutes * 60;
		}
		if (uptimeRaw > 0) {
			uptime = uptime + uptimeRaw + "s";
		}
		if (uptime == "") {
			uptime = "0s";
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
		eb.setColor(MessageUtils.randomColor());
		eb.addField("Author", "@Tis_awesomeness#8617", true);
		eb.addField("Version", Bot.getVersion(), true);
		eb.addField("Guilds", e.getJDA().getGuilds().size() + "", true);
		eb.addField("Channels", e.getJDA().getTextChannels().size() + "", true);
		eb.addField("Users", e.getJDA().getUsers().size() + "", true);
		eb.addField("Uptime", uptime, true);
		eb.addField("Memory", memory, true);
		eb.addField("Invite", Config.getInvite(), true);
		eb.addField("Help Server", helpServer, true);
		eb.addField("Credits", "Mojang API, Crafatar, and MCAPI", true);
		eb.addField("Library", "Java `1.8.0_101`, JDA `3.0.BETA2_135`", true);
		
		return new Result(Outcome.SUCCESS, eb.build());
	}

	private static String format(long value, long divider, String unit) {
	    double result = divider > 1 ? (double) value / (double) divider : (double) value;
	    return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
	
}
