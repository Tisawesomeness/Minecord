package com.tisawesomeness.minecord.command.core;

import com.tisawesomeness.minecord.BotBranding;
import com.tisawesomeness.minecord.Minecord;
import com.tisawesomeness.minecord.command.meta.CommandContext;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.share.BuildInfo;
import com.tisawesomeness.minecord.util.DateUtils;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class InfoCommand extends AbstractCoreCommand {

    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String JAVA_VENDOR = System.getProperty("java.vendor");
    private static final String OS_ARCH = System.getProperty("os.arch");
    private static final String OS_NAME = System.getProperty("os.name");
    private static final String OS_VERSION = System.getProperty("os.version");
    private static final BuildInfo buildInfo = BuildInfo.getInstance();

    public @NonNull String getId() {
        return "info";
    }

    public void run(String[] args, CommandContext ctx) {
        ctx.triggerCooldown();

        // If the author used the admin keyword and is an elevated user
        boolean elevated = false;
        if (args.length > 0 && "admin".equals(args[0]) && ctx.isElevated()) {
            elevated = true;
        }

        Config config = ctx.getConfig();
        BotBranding branding = ctx.getBot().getBranding();
        EmbedBuilder eb = new EmbedBuilder();

        eb.addField("Author", BotBranding.AUTHOR, true);
        if (config.isSelfHosted()) {
            eb.addField("Self-Hoster", branding.getAuthor(), true);
        }
        eb.addField("Version", MarkdownUtil.monospace(buildInfo.version), true);
        eb.addField("MC Version", MarkdownUtil.monospace(config.getSupportedMCVersion()), true);

        Minecord bot = ctx.getBot();
        ShardManager sm = bot.getShardManager();
        String guilds = String.valueOf(sm.getGuilds().size());
        int shardTotal = bot.getShardManager().getShardsTotal();
        if (shardTotal > 1) {
            String shards = ctx.getE().getJDA().getShardInfo().getShardId() + 1 + "/" + shardTotal;
            eb.addField("Shard", shards, true);
            guilds += " {" + ctx.getE().getJDA().getGuilds().size() + "}";
        }
        eb.addField("Guilds", guilds, true);

        eb.addField("Uptime", DateUtils.getDurationString(bot.getBirth()), true);
        eb.addField("Ping", sm.getAverageGatewayPing() + "ms", true);
        if (config.getFlagConfig().isShowExtraInfo() || elevated) {
            eb.addField("Memory", getMemoryString(), true);
            eb.addField("Boot Time", DateUtils.getBootTime(bot.getBootTime()), true);
            eb.addField("OS", OS_NAME, true);
            eb.addField("OS Arch", OS_ARCH, true);
            eb.addField("OS Version", MarkdownUtil.monospace(OS_VERSION), true);
            eb.addField("Java Vendor", JAVA_VENDOR, true);
        }
        eb.addField("Java Version", MarkdownUtil.monospace(JAVA_VERSION), true);
        eb.addField("JDA Version", MarkdownUtil.monospace(buildInfo.jdaVersion), true);

        String links = MarkdownUtil.maskedLink("INVITE", branding.getInvite()) + " | " +
            MarkdownUtil.maskedLink("SUPPORT", branding.getHelpServer()) + " | " +
            MarkdownUtil.maskedLink("WEBSITE", branding.getWebsite()) + " | " +
            MarkdownUtil.maskedLink("GITHUB", branding.getGithub());
        eb.addField("Links", "**" + links + "**", false);

        ctx.reply(eb);
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
