package com.tisawesomeness.minecord.command.misc;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

public class InfoCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"info",
			"Shows the bot info.",
			null,
			new String[]{"about", "stats"},
			0,
			false,
			false,
			true
		);
	}

	public String getAdminHelp() {
		return "`{&}info` - Shows the bot info.\n" +
			"`{&}info admin` - Include memory usage and boot time.\n";
	}

	public Result run(CommandContext txt) {
		ShardManager sm = txt.bot.getShardManager();
		DiscordUtils.update(sm, txt.config);
		
		// If the author used the admin keyword and is an elevated user
		boolean elevated = false;
		if (txt.args.length > 0 && txt.args[0].equals("admin") && txt.isElevated) {
			elevated = true;
		}
		
		// Build message
		EmbedBuilder eb = new EmbedBuilder();

		eb.addField("Author", Bot.author, true);
		eb.addField("Version", Bot.version, true);
		
		String guilds = sm.getGuilds().size() + "";
		int shardTotal = txt.bot.getShardManager().getShardsTotal();
		if (shardTotal > 1) {
			String shards = txt.e.getJDA().getShardInfo().getShardId() + 1 + "/" + shardTotal;
			eb.addField("Shard", shards, true);
			guilds += " {" + txt.e.getJDA().getGuilds().size() + "}";
		}
		eb.addField("Guilds", guilds + "", true);
		
		eb.addField("Uptime", DateUtils.getUptime(txt.bot.getBirth()), true);
		eb.addField("Ping", sm.getAverageGatewayPing() + "ms", true);
		if (txt.config.shouldShowMemory() || elevated) {
			eb.addField("Memory", getMemoryString(), true);
			eb.addField("Boot Time", DateUtils.getBootTime(txt.bot.getBootTime()), true);
		}
		eb.addField("Java Version", MarkdownUtil.monospace(Bot.javaVersion), true);
		eb.addField("JDA Version", MarkdownUtil.monospace(Bot.jdaVersion), true);

		String links = MarkdownUtil.maskedLink("INVITE", txt.config.getInvite()) + " | " +
			MarkdownUtil.maskedLink("SUPPORT", Bot.helpServer) + " | " +
			MarkdownUtil.maskedLink("WEBSITE", Bot.website) + " | " +
			MarkdownUtil.maskedLink("GITHUB", Bot.github);
		eb.addField("Links", "**" + links + "**", false);
		
		eb = txt.brand(eb);
		return new Result(Outcome.SUCCESS, eb.build());
	}

	// Calculate memory
	// From https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/3758880#3758880
	private static String getMemoryString() {
		long bytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		if (absB < 1024) {
			return bytes + " B";
		}
		long value = absB;
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
			value >>= 10;
			ci.next();
		}
		value *= Long.signum(bytes);
		return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}
	
}
