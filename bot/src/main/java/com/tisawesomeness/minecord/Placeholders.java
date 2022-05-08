package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.common.BuildInfo;
import com.tisawesomeness.minecord.config.config.Config;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

public final class Placeholders {
    // Branding
    public static final String AUTHOR = "%author%";
    public static final String AUTHOR_TAG = "%author_tag%";
    public static final String INVITE = "%invite%";
    public static final String HELP_SERVER = "%help_server%";
    public static final String WEBSITE = "%website%";
    public static final String GITHUB = "%github%";
    // BuildInfo
    public static final String VERSION = "%version%";
    public static final String JDA_VERSION = "%jda_version%";
    // Other constants
    public static final String JAVA_VERSION = "%java_version%";
    public static final String MC_VERSION = "%mc_version%";
    public static final String BOT_SHARDS = "%bot_shards%";
    public static final String PREFIX = "%prefix%";
    // Variables
    public static final String GUILDS = "%guilds%";
    public static final String BOT_USERNAME = "%bot_username%";
    public static final String BOT_TAG = "%bot_tag%";
    public static final String BOT_MENTION = "%bot_mention%";
    public static final String BOT_ID = "%bot_id%";

    private static final BuildInfo buildInfo = BuildInfo.getInstance();

    /**
     * Replaces constants in the input string with their values
     * @param str A string with %constants%
     * @param config The config file to get the default prefix from
     * @return The string with resolved constants, though variables such as %guilds% are unresolved
     */
    public static @NonNull String parseConstants(@NonNull String str, @NonNull Config config,
                                                 @NonNull BotBranding botBranding, int shardCount) {
        return botBranding.parsePlaceholders(str)
                .replace(VERSION, buildInfo.version)
                .replace(JDA_VERSION, buildInfo.jdaVersion)
                .replace(JAVA_VERSION, System.getProperty("java.version"))
                .replace(MC_VERSION, config.getSupportedMCVersion())
                .replace(BOT_SHARDS, String.valueOf(shardCount))
                .replace(PREFIX, config.getSettingsConfig().getDefaultPrefix());
    }
    /**
     * Replaces variables in the input string with their values
     * @param str A string with %variables%
     * @return The string with resolved variables, though constants such as %version% are unresolved
     */
    public static @NonNull String parseVariables(@NonNull String str, @NonNull ShardManager sm) {
        User u = sm.getShards().get(0).getSelfUser();
        return str.replace(GUILDS, String.valueOf(sm.getGuildCache().size()))
                .replace(BOT_USERNAME, u.getName())
                .replace(BOT_TAG, u.getAsTag())
                .replace(BOT_MENTION, u.getAsMention())
                .replace(BOT_ID, u.getId());
    }

}
