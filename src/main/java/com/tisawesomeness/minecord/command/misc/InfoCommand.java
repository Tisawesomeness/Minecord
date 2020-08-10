package com.tisawesomeness.minecord.command.misc;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.BuildInfo;
import com.tisawesomeness.minecord.command.CommandContext;
import com.tisawesomeness.minecord.command.Result;
import com.tisawesomeness.minecord.util.DateUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class InfoCommand extends AbstractMiscCommand {

    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String JAVA_VENDOR = System.getProperty("java.vendor");
    private static final String OS_ARCH = System.getProperty("os.arch");
    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_VERSION = System.getProperty("os.version");

    public @NonNull String getId() {
        return "info";
    }

    public Result run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();
        ShardManager sm = ctx.bot.getShardManager();

        // If the author used the admin keyword and is an elevated user
        boolean elevated = false;
        if (args.length > 0 && "admin".equals(args[0]) && ctx.isElevated) {
            elevated = true;
        }

        // Build message
        EmbedBuilder eb = new EmbedBuilder();

        eb.addField("Author", Bot.author, true);
        eb.addField("Version", MarkdownUtil.monospace(BuildInfo.getInstance().version), true);

        String guilds = String.valueOf(sm.getGuilds().size());
        int shardTotal = ctx.bot.getShardManager().getShardsTotal();
        if (shardTotal > 1) {
            String shards = ctx.e.getJDA().getShardInfo().getShardId() + 1 + "/" + shardTotal;
            eb.addField("Shard", shards, true);
            guilds += " {" + ctx.e.getJDA().getGuilds().size() + "}";
        }
        eb.addField("Guilds", guilds, true);

        eb.addField("Uptime", DateUtils.getDurationString(ctx.bot.getBirth()), true);
        eb.addField("Ping", sm.getAverageGatewayPing() + "ms", true);
        if (ctx.config.getFlagConfig().isShowExtraInfo() || elevated) {
            eb.addField("Memory", getMemoryString(), true);
            eb.addField("Boot Time", DateUtils.getBootTime(ctx.bot.getBootTime()), true);
            eb.addField("OS", OS_NAME, true);
            eb.addField("OS Arch", OS_ARCH, true);
            eb.addField("OS Version", MarkdownUtil.monospace(OS_VERSION), true);
            eb.addField("Java Vendor", JAVA_VENDOR, true);
        }
        eb.addField("Java Version", MarkdownUtil.monospace(JAVA_VERSION), true);
        eb.addField("JDA Version", MarkdownUtil.monospace(Bot.jdaVersion), true);

        String links = MarkdownUtil.maskedLink("INVITE", ctx.config.getInviteLink()) + " | " +
            MarkdownUtil.maskedLink("SUPPORT", Bot.helpServer) + " | " +
            MarkdownUtil.maskedLink("WEBSITE", Bot.website) + " | " +
            MarkdownUtil.maskedLink("GITHUB", Bot.github);
        eb.addField("Links", "**" + links + "**", false);

        return ctx.reply(eb);
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
