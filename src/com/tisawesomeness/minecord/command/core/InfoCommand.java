package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.command.SlashCommand;
import com.tisawesomeness.minecord.util.DateUtils;
import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.MessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class InfoCommand extends SlashCommand {

    private static final String JAVA_VERSION = System.getProperty("java.version");

    private static final String LEGAL = MarkdownUtil.maskedLink("TERMS", Bot.terms) +
            " | " + MarkdownUtil.maskedLink("PRIVACY", Bot.privacy);
    private static final String DONATE = ":sparkles: " + MarkdownUtil.bold(MarkdownUtil.maskedLink("Donate", Bot.donate)) +
            " :sparkles: to support development!";

    public CommandInfo getInfo() {
        return new CommandInfo(
                "info",
                "Shows the bot info.",
                null,
                0,
                false,
                false
        );
    }

    public static String[] legacyAliases() {
        return new String[]{"about", "stats"};
    }
    @Override
    public String[] getLegacyAliases() {
        return legacyAliases();
    }

    public Result run(SlashCommandInteractionEvent e) {
        return run(false, e.getJDA());
    }

    public static Result run(boolean elevated, JDA jda) {
        DiscordUtils.update();

        // Build message
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Bot.color);
        eb.addField("Author", Bot.author, true);
        if (Config.isSelfHosted()) {
            eb.addField("Self-Hoster", Config.getAuthor(), true);
        }
        eb.addField("Version", MarkdownUtil.monospace(Bot.version), true);

        String guilds = String.valueOf(Bot.getGuildCount());
        int shardCount = jda.getShardInfo().getShardTotal();
        if (shardCount > 1) {
            String shards = jda.getShardInfo().getShardId() + 1 + "/" + shardCount;
            eb.addField("Shard", shards, true);
            guilds += " {" + jda.getGuilds().size() + "}";
        }
        eb.addField("Guilds", guilds, true);

        eb.addField("Uptime", DateUtils.getUptime(), true);
        eb.addField("Ping", (int) Math.ceil(Bot.getPing()) + "ms", true);
        if (Config.getShowMemory() || elevated) {
            eb.addField("Memory", getMemoryString(), true);
            eb.addField("Boot Time", DateUtils.getBootTime(), true);
        }
        eb.addField("Java Version", MarkdownUtil.monospace(JAVA_VERSION), true);
        eb.addField("JDA Version", MarkdownUtil.monospace(Bot.jdaVersion), true);

        String links = MarkdownUtil.maskedLink("INVITE", Config.getInvite()) +
                " | " + MarkdownUtil.maskedLink("SUPPORT", Config.getHelpServer()) +
                " | " + MarkdownUtil.maskedLink("WEBSITE", Config.getWebsite()) +
                " | " + MarkdownUtil.maskedLink("GITHUB", Config.getGithub());
        eb.addField("Links", MarkdownUtil.bold(links), false);

        if (!Config.isSelfHosted()) {
            eb.addField("Legal", MarkdownUtil.bold(LEGAL), false);
            eb.addField("Donate", DONATE, false);
        }

        eb = MessageUtils.addFooter(eb);
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
