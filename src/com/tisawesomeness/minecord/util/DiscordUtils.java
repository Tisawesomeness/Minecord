package com.tisawesomeness.minecord.util;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtils {

    private static final Pattern ID_PATTERN = Pattern.compile("[0-9]{2,32}");
    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("(<@!?)?([0-9]{2,32})>?");
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("(<#)?([0-9]{2,32})>?");

    public static boolean isDiscordId(String str) {
        return ID_PATTERN.matcher(str).matches();
    }

    public static String tagAndId(User u) {
        return String.format("%#s (`%s`)", u, u.getId());
    }

    public static void update() {
        Bot.shardManager.setActivity(Activity.playing(parseAll(Config.getGame())));
    }

    /**
     * Replaces constants in the input string with their values
     * This can be called during init, as long as Config is initialized
     * @param input A string with {constants}
     * @return The string with resolved constants, though variables such as {guilds} are unresolved
     */
    public static String parseConstants(String input) {
        return input
                .replace("{author}", Config.getAuthor())
                .replace("{author_tag}", Config.getAuthorTag())
                .replace("{invite}", Config.getInvite())
                .replace("{help_server}", Config.getHelpServer())
                .replace("{website}", Config.getWebsite())
                .replace("{github}", Config.getGithub())
                .replace("{donate}", Bot.donate)
                .replace("{terms}", Bot.terms)
                .replace("{privacy}", Bot.privacy)
                .replace("{prefix}", Config.getPrefix())
                .replace("{jda_ver}", Bot.jdaVersion)
                .replace("{version}", Bot.version);
    }

    /**
     * Replaces variables in the input string with their values
     * This must be called after init
     * @param input A string with {variables}
     * @return The string with resolved variables, though constants such as {version} are unresolved
     */
    public static String parseVariables(String input) {
        return input.replace("{guilds}", String.valueOf(Bot.shardManager.getGuilds().size()));
    }

    /**
     * Replaces variables and constants in the input string with their values
     * This must be called after init
     * @param input A string with {variables}
     * @return The string with resolved variables
     */
    public static String parseAll(String input) {
        return parseVariables(parseConstants(input));
    }

    public static Set<IntegrationType> getInstallTypes(Interaction e) {
        Set<IntegrationType> types = EnumSet.noneOf(IntegrationType.class);
        IntegrationOwners owners = e.getIntegrationOwners();
        if (owners.getAuthorizingGuildId() != null) {
            types.add(IntegrationType.GUILD_INSTALL);
        }
        if (owners.getAuthorizingUserId() != null) {
            types.add(IntegrationType.USER_INSTALL);
        }
        return types;
    }

    public static User findUser(String search) {
        if (isDiscordId(search)) {
            return Bot.shardManager.retrieveUserById(search).complete();
        }
        Matcher ma = USER_MENTION_PATTERN.matcher(search);
        if (!ma.matches()) {
            return null;
        }
        return Bot.shardManager.retrieveUserById(ma.group(2))
                .onErrorMap(ErrorResponse.UNKNOWN_USER::test, x -> null)
                .onErrorMap(ErrorResponse.UNKNOWN_MEMBER::test, x -> null)
                .complete();
    }

    public static TextChannel findChannel(String search) {
        if (isDiscordId(search)) {
            return Bot.shardManager.getTextChannelById(search);
        }
        Matcher ma = CHANNEL_MENTION_PATTERN.matcher(search);
        return ma.matches() ? Bot.shardManager.getTextChannelById(ma.group(2)) : null;
    }

    /**
     * Gets the emote text associated with true or false.
     */
    public static String getBoolEmote(boolean bool) {
        return bool ? ":white_check_mark:" : ":x:";
    }

}
