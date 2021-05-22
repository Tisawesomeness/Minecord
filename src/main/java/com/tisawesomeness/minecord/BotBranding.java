package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.branding.BrandingConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.config.config.SettingsConfig;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.awt.Color;

/**
 * Holds the bot branding information.
 * Static constants are the original branding, instance methods change depending on whether the bot is self-hosted
 */
public class BotBranding {

    private static final BuildInfo buildInfo = BuildInfo.getInstance();

    public static final String AUTHOR = "Tis_awesomeness";
    public static final String AUTHOR_TAG = "@Tis_awesomeness#8617";
    public static final String INVITE = "https://minecord.github.io/invite";
    public static final String HELP_SERVER = "https://minecord.github.io/support";
    public static final String WEBSITE = "https://minecord.github.io";
    public static final String GITHUB = "https://github.com/Tisawesomeness/Minecord";

    @Getter private final @NonNull String author;
    @Getter private final @NonNull String authorTag;
    @Getter private final @NonNull String invite;
    @Getter private final @NonNull String helpServer;
    @Getter private final @NonNull String website;
    @Getter private final @NonNull String github;

    /**
     * Creates branding that uses the default values
     */
    public BotBranding() {
        author = AUTHOR;
        authorTag = AUTHOR_TAG;
        invite = INVITE;
        helpServer = HELP_SERVER;
        website = WEBSITE;
        github = GITHUB;
    }
    /**
     * Creates branding that pulls from the branding config if not self-hosted
     * @param config The config
     * @param branding The branding config
     */
    public BotBranding(@NonNull Config config, @NonNull Branding branding) {
        if (config.isSelfHosted() && branding.getBrandingConfig() != null) {
            BrandingConfig bc = branding.getBrandingConfig();
            author = bc.getAuthor();
            authorTag = bc.getAuthorTag();
            invite = bc.getInvite();
            helpServer = bc.getHelpServer();
            website = bc.getWebsite();
            github = bc.getGithub();
        } else {
            author = AUTHOR;
            authorTag = AUTHOR_TAG;
            invite = INVITE;
            helpServer = HELP_SERVER;
            website = WEBSITE;
            github = GITHUB;
        }
    }

    public @NonNull Color getColor() {
        return Color.GREEN;
    }

    /**
     * Replaces constants in the input string with their values
     * @param str A string with %constants%
     * @param config The config file to get the default prefix from
     * @return The string with resolved constants, though variables such as %guilds% are unresolved
     */
    public @NonNull String parseConstants(@NonNull String str, @NonNull SettingsConfig config) {
        return buildInfo.parsePlaceholders(str)
                .replace("%author%", author)
                .replace("%author_tag%", authorTag)
                .replace("%help_server%", helpServer)
                .replace("%invite%", invite)
                .replace("%website%", website)
                .replace("%github%", github)
                .replace("%prefix%", config.getDefaultPrefix());
    }
    /**
     * Replaces variables in the input string with their values
     * @param str A string with %variables%
     * @return The string with resolved variables, though constants such as %version% are unresolved
     */
    public static String parseVariables(String str, ShardManager sm) {
        return str.replace("%guilds%", String.valueOf(sm.getGuildCache().size()));
    }

}
