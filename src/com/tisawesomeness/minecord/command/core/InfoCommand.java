package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

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

    public Result run(String[] args, MessageReceivedEvent e) {
        DiscordUtils.update();

        // If the author used the admin keyword and is an elevated user
        boolean elevated = args.length > 0 && args[0].equals("admin") && Database.isElevated(e.getAuthor().getIdLong());

        // Build message
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Bot.color);
        eb.addField("Author", Bot.author, true);
        if (Config.isIsSelfHosted()) {
            eb.addField("Self-Hoster", Config.getAuthor(), true);
        }
        eb.addField("Version", Bot.getVersion(), true);

        String guilds = String.valueOf(Bot.shardManager.getGuilds().size());
        if (Config.getShardCount() > 1) {
            String shards = e.getJDA().getShardInfo().getShardId() + 1 + "/" + Config.getShardCount();
            eb.addField("Shard", shards, true);
            guilds += " {" + e.getJDA().getGuilds().size() + "}";
        }
        eb.addField("Guilds", guilds, true);

        eb.addField("Uptime", DateUtils.getUptime(), true);
        eb.addField("Ping", Bot.shardManager.getAverageGatewayPing() + "ms", true);
        if (Config.getShowMemory() || elevated) {
            eb.addField("Memory", getMemoryString(), true);
            eb.addField("Boot Time", DateUtils.getBootTime(), true);
        }
        eb.addField("Java Version", MarkdownUtil.monospace(Bot.javaVersion), true);
        eb.addField("JDA Version", MarkdownUtil.monospace(Bot.jdaVersion), true);

        String links = MarkdownUtil.maskedLink("INVITE", Config.getInvite()) + " | " +
                MarkdownUtil.maskedLink("SUPPORT", Config.getHelpServer()) + " | " +
                MarkdownUtil.maskedLink("WEBSITE", Config.getWebsite()) + " | " +
                MarkdownUtil.maskedLink("GITHUB", Config.getGithub());
        eb.addField("Links", "**" + links + "**", false);

        eb = MessageUtils.addFooter(eb);
        return new Result(Outcome.SUCCESS, eb.build());
    }

    // Calculate memory
    // From https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/3758880#3758880
    public static String getMemoryString() {
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
