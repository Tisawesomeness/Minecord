package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains all the methods representing the context a command is running in.
 * <br>Normally, this is a Discord {@link net.dv8tion.jda.api.entities.MessageChannel}, which may or may not be a DM.
 * <br>Commands can expect all methods to work in any non-testing environment.
 */
public interface CommandContext {

    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    String[] getArgs();
    /**
     * The event that triggered the command.
     */
    @NonNull MessageReceivedEvent getE();
    /**
     * The loaded config.
     */
    @NonNull Config getConfig();
    /**
     * A link to the bot instance.
     */
    @NonNull Bot getBot();
    /**
     * The original command that created this context
     */
    @NonNull Command getCmd();
    /**
     * The executor in charge of running this command and tracking cooldowns
     */
    @NonNull CommandExecutor getExecutor();
    /**
     * Whether the user executing the command is elevated.
     */
    boolean isElevated();
    /**
     * The current prefix.
     */
    @NonNull String getPrefix();
    /**
     * The current language. Use {@link #i18n(String)} as a shortcut for {@link Lang#i18n(String) lang.get(String)}.
     */
    Lang getLang();

    /**
     * Replies to the sender of this command.
     * @param text The text to send. Must be shorter than {@link Message#MAX_CONTENT_LENGTH}.
     * @return Success
     */
    Result reply(@NonNull CharSequence text);
    /**
     * Replies to the sender of this command with an embed.
     * <br>The embed is sent with no modifications.
     * @param eb The embed builder to be built and sent. Must satisfy all ebmed length limits.
     * @return Success
     */
    Result replyRaw(@NonNull EmbedBuilder eb);
    /**
     * Replies to the sender of this command with an embed.
     * <br>The bot color and a random announcement (if enabled) are added.
     * @param eb The embed builder to be built and sent. Must satisfy all ebmed length limits.
     * @return Success
     */
    default Result reply(@NonNull EmbedBuilder eb) {
        return replyRaw(brand(eb));
    }
    /**
     * Creates an embed with the current command's help.
     * @return The Help result
     */
    Result showHelp();

    /**
     * Tells the sender of this command they used it wrong.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Invalid args result
     */
    default Result invalidArgs(@NonNull CharSequence text) {
        return sendResult(Result.INVALID_ARGS, text);
    }
    /**
     * Warns the sender of this command.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Warning
     */
    default Result warn(@NonNull CharSequence text) {
        return sendResult(Result.WARNING, text);
    }
    /**
     * Warns the sender of this command, but keeps in mind the possibility it may actually be an error.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Possible Error
     */
    default Result possibleErr(@NonNull CharSequence text) {
        return sendResult(Result.POSSIBLE_ERROR, text);
    }
    /**
     * Displays an error to the sender of this command.
     * <br>The error emote is added to the message.
     * @param text The error message
     * @return Error
     */
    default Result err(@NonNull CharSequence text) {
        return sendResult(Result.ERROR, text);
    }
    /**
     * Tells the sender of this command that they need to be elevated.
     * <br>The not elevated emote is added to the message.
     * @param text The warning message
     * @return Not elevated permissions result
     */
    default Result notElevated(@NonNull CharSequence text) {
        return sendResult(Result.NOT_ELEVATED, text);
    }
    /**
     * Tells the sender of this command that they do not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     * @return No user permissions result
     */
    default Result noUserPermissions(@NonNull CharSequence text) {
        return sendResult(Result.NO_USER_PERMISSIONS, text);
    }
    /**
     * Tells the sender of this command that the bot does not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     * @return No bot permissions result
     */
    default Result noBotPermissions(@NonNull CharSequence text) {
        return sendResult(Result.NO_BOT_PERMISSIONS, text);
    }

    /**
     * Sends a message to the current channel, adding the appropiate emote.
     * @param result The result to get the emote from
     * @param text The message to send
     * @return The given result
     */
    Result sendResult(Result result, @NonNull CharSequence text);

    /**
     * Starts the cooldown timer for this command, unless the user is elevated and skipping cooldowns is enabled.
     */
    void triggerCooldown();

    /**
     * Adds the footer with the rolled announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added footer.
     */
    @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb);
    /**
     * Adds the bot color and a random announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added branding.
     */
    @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb);

    /**
     * @return True if the command was executed in a guild
     */
    boolean isFromGuild();

    /**
     * Checks if the user has all permissions in the current channel.
     * @param permissions A list of permissions
     * @return True if the user is elevated or has all permissions in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    boolean userHasPermission(Permission... permissions);
    /**
     * Checks if the user has all permissions in the current channel.
     * @param permissions A list of permissions
     * @return True if the user is elevated or has all permissions in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    boolean userHasPermission(Collection<Permission> permissions);
    /**
     * Checks if the bot has all permissions in the current channel.
     * <br><b>Do not assume the bot has every permission requested in the invite.</b>
     * @param permissions A list of permissions
     * @return True only if the bot has every permission in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    boolean botHasPermission(Permission... permissions);
    /**
     * Checks if the bot has all permissions in the current channel.
     * <br><b>Do not assume the bot has every permission requested in the invite.</b>
     * @param permissions A list of permissions
     * @return True only if the bot has every permission in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    boolean botHasPermission(Collection<Permission> permissions);

    /**
     * Equivalent to {@code String.join(" ", ctx.getArgs())}
     * @return All arguments as a single string
     */
    default @NonNull String joinArgs() {
        return String.join(" ", getArgs());
    }
    /**
     * Creates a string with arguments from {@code beginIndex} to the end of the array.
     * @param beginIndex The nonnegative starting index
     * @return A string with joined arguments, or empty if {@code beginIndex >= ctx.args.length}
     * @throws IllegalArgumentException If {@code beginIndex < 0}
     */
    default @NonNull String joinArgsSlice(int beginIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex was " + beginIndex + " but must be nonnegative.");
        }
        return Arrays.stream(getArgs())
                .skip(beginIndex)
                .collect(Collectors.joining(" "));
    }
    /**
     * Creates a string with arguments from {@code beginIndex} to {@code endIndex - 1}.
     * @param beginIndex The nonnegative starting index
     * @param endIndex The ending index, must be greater than or equal to {@code beginIndex}, may be out of bounds
     * @return A string with joined arguments, or empty if {@code beginIndex >= ctx.args.length}
     * @throws IllegalArgumentException If {@code beginIndex < 0} or {@code endIndex < beginIndex}
     */
    default @NonNull String joinArgsSlice(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex was " + beginIndex + " but must be nonnegative.");
        }
        if (endIndex < beginIndex) {
            throw new IllegalArgumentException(
                    String.format("endIndex (%d) must be >= beginIndex (%d)", endIndex, beginIndex));
        }
        return Arrays.stream(getArgs())
                .skip(beginIndex)
                .limit(endIndex - beginIndex)
                .collect(Collectors.joining(" "));
    }

    boolean shouldUseMenus();

    /**
     * The current locale, used in formatters.
     */
    default @NonNull Locale getLocale() {
        return getLang().getLocale();
    }
    /**
     * Gets a locallized string for the current lang.
     * @param key The <b>case-sensitive</b> localization key
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    default @NonNull String i18n(@NonNull String key) {
        return getCmd().i18n(getLang(), key);
    }
    /**
     * Gets a locallized, formatted string for the current lang.
     * @param key The <b>case-sensitive</b> localization key
     * @param args An ordered list of arguments to place into the string
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    default @NonNull String i18nf(@NonNull String key, Object... args) {
        return getCmd().i18nf(getLang(), key, args);
    }

    /**
     * Logs a message to the logging channel.
     */
    void log(@NonNull String m);
    /**
     * Logs a message to the logging channel.
     */
    void log(@NonNull Message m);
    /**
     * Logs a message to the logging channel.
     */
    void log(@NonNull MessageEmbed m);

    /**
     * Shortcut for {@link #getBot}.{@link Bot#getDatabaseCache() getDatabase()}
     * @return The guild, channel, and user cache associated with this bot
     */
    default @NonNull DatabaseCache getCache() {
        return getBot().getDatabaseCache();
    }

    /**
     * Gets a guild from the backend
     * @param gid The guild id
     * @return Either the cached guild or one with default settings
     */
    default @NonNull DbGuild getGuild(long gid) {
        return getCache().getGuild(gid);
    }
    /**
     * Gets a guild from the backend
     * @param g The JDA guild object
     * @return Either the cached guild or one with default settings
     */
    default @NonNull DbGuild getGuild(@NonNull Guild g) {
        return getGuild(g.getIdLong());
    }

    /**
     * Gets a channel from the backend
     * <br>If the guild id is known, use {@link #getChannel(long, long)}.
     * @param cid The channel id
     * @return Either the cached channel, or empty since the guild ID must be known to create a new channel
     */
    default Optional<DbChannel> getChannel(long cid) {
        return getCache().getChannel(cid);
    }
    /**
     * Gets a channel from the backend
     * @param cid The channel id
     * @param gid The guild id
     * @return Either the cached channel or one with default settings
     */
    default @NonNull DbChannel getChannel(long cid, long gid) {
        return getCache().getChannel(cid, gid);
    }
    /**
     * Gets a channel from the backend
     * @param c The channel
     * @return Either the cached channel or one with default settings
     */
    default @NonNull DbChannel getChannel(@NonNull TextChannel c) {
        return getChannel(c.getIdLong(), c.getGuild().getIdLong());
    }
    /**
     * Gets all channels in the database that have the specified guild id.
     * @param gid The guild id
     * @return A possibly-empty list of channels
     */
    default List<DbChannel> getChannelsInGuild(long gid) {
        return getCache().getChannelsInGuild(gid);
    }

    /**
     * Gets a user from the backend
     * @param uid The user id
     * @return Either the cached user or one with default settings
     */
    default @NonNull DbUser getUser(long uid) {
        return getCache().getUser(uid);
    }
    /**
     * Gets a user from the backend
     * @param u The JDA user object
     * @return Either the cached user or one with default settings
     */
    default @NonNull DbUser getUser(@NonNull User u) {
        return getUser(u.getIdLong());
    }

}
