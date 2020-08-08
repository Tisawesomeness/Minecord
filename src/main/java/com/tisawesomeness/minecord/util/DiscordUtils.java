package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.BuildInfo;
import com.tisawesomeness.minecord.config.serial.Config;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiscordUtils {

    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}");

    public static final Pattern ANY_MENTION = Pattern.compile("<(@(!?|&)|#|:(.{2,32}):)\\d{17,20}>");

    /**
     * Replaces constants in the input string with their values
     * @param input A string with {constants}
     * @param config The config file to get the invite and default prefix from
     * @return The string with resolved constants, though variables such as {guilds} are unresolved
     */
    public static String parseConstants(String input, Config config) {
    return input
            .replace("{author}", Bot.author)
            .replace("{author_tag}", Bot.authorTag)
            .replace("{help_server}", Bot.helpServer)
            .replace("{website}", Bot.website)
            .replace("{github}", Bot.github)
            .replace("{jda_ver}", Bot.jdaVersion)
            .replace("{version}", BuildInfo.getInstance().version)
            .replace("{invite}", config.getInviteLink())
            .replace("{prefix}", config.getSettingsConfig().getDefaultPrefix());
    }

    /**
     * Replaces variables in the input string with their values
     * @param input A string with {variables}
     * @return The string with resolved variables, though constants such as {version} are unresolved
     */
    public static String parseVariables(String input, ShardManager sm) {
        return input.replace("{guilds}", String.valueOf(sm.getGuildCache().size()));
    }

    public static User findUser(String search, ShardManager sm) {
        Matcher ma = Pattern.compile("(<@!?)?([0-9]{9,20})>?").matcher(search);
        return ma.matches() ? sm.getUserById(ma.group(2)) : null;
    }

    public static TextChannel findChannel(String search, ShardManager sm) {
        Matcher ma = Pattern.compile("(<#)?([0-9]{9,20})>?").matcher(search);
        return ma.matches() ? sm.getTextChannelById(ma.group(2)) : null;
    }

    /**
     * Checks if the given string is in the correct ID format.
     * <br>This does not necessarily mean the ID correspond to a real Discord snowflake.
     * <br>If true, {@link Long#parseLong(String id)} will not fail.
     * @param id The string id
     * @return True if the string only contains 17-20 digits
     */
    public static boolean isDiscordId(CharSequence id) {
        return DISCORD_ID.matcher(id).matches();
    }

}
