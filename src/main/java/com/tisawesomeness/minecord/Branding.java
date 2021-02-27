package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.serial.BrandingConfig;
import com.tisawesomeness.minecord.config.serial.Config;

import lombok.Getter;
import lombok.NonNull;

import java.awt.Color;

/**
 * Holds the bot branding information.
 * Static constants are the original branding, instance methods change depending on whether the bot is self-hosted
 */
public class Branding {

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

    public Branding(Config config) {
        if (config.isSelfHosted()) {
            BrandingConfig bc = config.getBrandingConfig();
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

}
