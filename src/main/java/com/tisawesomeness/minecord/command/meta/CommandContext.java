package com.tisawesomeness.minecord.command.meta;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.util.concurrent.FutureCallback;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Contains all the methods representing the context a command is running in.
 * <br>Normally, this is a Discord {@link net.dv8tion.jda.api.entities.MessageChannel}, which may or may not be a DM.
 * <br>Commands can expect all methods to work in any non-testing environment.
 */
@Slf4j
public abstract class CommandContext {

    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    public abstract String[] getArgs();
    /**
     * The event that triggered the command.
     */
    public abstract @NonNull MessageReceivedEvent getE();
    /**
     * The loaded config.
     */
    public abstract @NonNull Config getConfig();
    /**
     * A link to the bot instance.
     */
    public abstract @NonNull Bot getBot();
    /**
     * The original command that created this context
     */
    public abstract @NonNull Command getCmd();
    /**
     * The executor in charge of running this command and tracking cooldowns
     */
    public abstract @NonNull CommandExecutor getExecutor();
    /**
     * Whether the user executing the command is elevated.
     */
    public abstract boolean isElevated();
    /**
     * The current prefix.
     */
    public abstract @NonNull String getPrefix();
    /**
     * The current language. Use {@link #i18n(String)} as a shortcut for {@link Lang#i18n(String) lang.get(String)}.
     */
    public abstract Lang getLang();

    /**
     * Sends a message to the sender of this command. This does not keep track of results.
     * @param text The text to send. Must satisfy all text length limits.
     */
    protected abstract void sendMessage(@NonNull CharSequence text);
    /**
     * Sends a message to the sender of this command with an embed. This does not keep track of results.
     * <br>The embed is sent with no modifications.
     * @param emb The embed to be sent. Must satisfy all embed length limits.
     */
    protected abstract void sendMessage(@NonNull MessageEmbed emb);
    /**
     * Sends the current command's help message to the sender of this command. This does not keep track of results.
     */
    protected abstract void requestHelp();

    /**
     * Replies to the sender of this command.
     * @param text The text to send. Must satisfy all text length limits.
     */
    public void reply(@NonNull CharSequence text) {
        sendMessage(text);
        commandResult(Result.SUCCESS);
    }
    /**
     * Replies to the sender of this command with an embed.
     * <br>The embed is sent with no modifications.
     * @param eb The embed builder to be built and sent. Must satisfy all embed length limits.
     */
    public void replyRaw(@NonNull EmbedBuilder eb) {
        reply(eb.build());
    }
    /**
     * Replies to the sender of this command with an embed.
     * <br>The bot color and a random announcement (if enabled) are added.
     * @param eb The embed builder to be built and sent. Must satisfy all embed length limits.
     */
    public void reply(@NonNull EmbedBuilder eb) {
        replyRaw(brand(eb));
    }
    /**
     * Replies to the sender of this command with an embed.
     * <br>The bot color and a random announcement (if enabled) are added.
     * @param emb The embed to be sent. Must satisfy all embed length limits.
     */
    public void reply(@NonNull MessageEmbed emb) {
        sendMessage(emb);
        commandResult(Result.SUCCESS);
    }
    /**
     * Replies with the current command's help.
     */
    public void showHelp() {
        requestHelp();
        commandResult(Result.HELP);
    }

    /**
     * Tells the sender of this command they used it wrong.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     */
    public void invalidArgs(@NonNull CharSequence text) {
        sendResult(Result.INVALID_ARGS, text);
    }
    /**
     * Warns the sender of this command.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     */
    public void warn(@NonNull CharSequence text) {
        sendResult(Result.WARNING, text);
    }
    /**
     * Warns the sender of this command, but keeps in mind the possibility it may actually be an error.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     */
    public void possibleErr(@NonNull CharSequence text) {
        sendResult(Result.POSSIBLE_ERROR, text);
    }
    /**
     * Displays an error to the sender of this command.
     * <br>The error emote is added to the message.
     * @param text The error message
     */
    public void err(@NonNull CharSequence text) {
        sendResult(Result.ERROR, text);
    }
    /**
     * Tells the sender of this command that they can only use the command in a guild.
     * <br>The guild only emote is added to the message.
     * @param text The warning message
     */
    public void guildOnly(@NonNull CharSequence text) {
        sendResult(Result.GUILD_ONLY, text);
    }
    /**
     * Tells the sender of this command that they need to be elevated.
     * <br>The not elevated emote is added to the message.
     * @param text The warning message
     */
    public void notElevated(@NonNull CharSequence text) {
        sendResult(Result.NOT_ELEVATED, text);
    }
    /**
     * Tells the sender of this command that they do not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     */
    public void noUserPermissions(@NonNull CharSequence text) {
        sendResult(Result.NO_USER_PERMISSIONS, text);
    }
    /**
     * Tells the sender of this command that the bot does not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     */
    public void noBotPermissions(@NonNull CharSequence text) {
        sendResult(Result.NO_BOT_PERMISSIONS, text);
    }

    /**
     * Sends a message to the current channel, adding the appropiate emote.
     * @param result The result to get the emote from
     * @param text The message to send
     */
    public void sendResult(Result result, @NonNull CharSequence text) {
        sendMessage(result.addEmote(text));
        commandResult(result);
    }

    /**
     * Reports the result of this command. This is done automatically in {@code reply()}, {@code warn()}, and other
     * related methods, so this can be used to report the result if there is no reply or to override the result.
     * @param result The result to report
     */
    public void commandResult(Result result) {
        getExecutor().pushResult(getCmd(), result);
    }

    /**
     * Starts the cooldown timer for this command, unless the user is elevated and skipping cooldowns is enabled.
     */
    public abstract void triggerCooldown();

    /**
     * Adds the footer with the rolled announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added footer.
     */
    public abstract @NonNull EmbedBuilder addFooter(@NonNull EmbedBuilder eb);
    /**
     * @return The bot color
     */
    public @NonNull Color getColor() {
        return getBot().getBranding().getColor();
    }
    /**
     * Adds the bot color and a random announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added branding.
     */
    public @NonNull EmbedBuilder brand(@NonNull EmbedBuilder eb) {
        return addFooter(eb.setColor(getColor()));
    }

    /**
     * @return True if the command was executed in a guild
     */
    public abstract boolean isFromGuild();

    /**
     * Checks if the user has all permissions in the current channel.
     * @param permissions A list of permissions
     * @return True if the user is elevated or has all permissions in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public abstract boolean userHasPermission(Permission... permissions);
    /**
     * Checks if the user has all permissions in the current channel.
     * @param permissions A list of permissions
     * @return True if the user is elevated or has all permissions in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public abstract boolean userHasPermission(Collection<Permission> permissions);
    /**
     * Checks if the bot has all permissions in the current channel.
     * <br><b>Do not assume the bot has every permission requested in the invite.</b>
     * @param permissions A list of permissions
     * @return True only if the bot has every permission in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public abstract boolean botHasPermission(Permission... permissions);
    /**
     * Checks if the bot has all permissions in the current channel.
     * <br><b>Do not assume the bot has every permission requested in the invite.</b>
     * @param permissions A list of permissions
     * @return True only if the bot has every permission in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public abstract boolean botHasPermission(Collection<Permission> permissions);

    /**
     * Formats the executing command to "&name" according to the current prefix and lang.
     * @return The equivalent of "&name"
     */
    public @NonNull String formatCommandName() {
        return formatCommandName(getCmd());
    }
    /**
     * Formats a command to "&name" according to the current prefix and lang.
     * @param c A command
     * @return The equivalent of "&name"
     */
    public @NonNull String formatCommandName(@NonNull Command c) {
        if (c.isEnabled(getConfig().getCommandConfig())) {
            return String.format("`%s%s`", getPrefix(), c.getDisplayName(getLang()));
        }
        return String.format("~~`%s%s`~~", getPrefix(), c.getDisplayName(getLang()));
    }

    /**
     * Equivalent to {@code String.join(" ", ctx.getArgs())}
     * @return All arguments as a single string
     */
    public @NonNull String joinArgs() {
        return String.join(" ", getArgs());
    }
    /**
     * Creates a string with arguments from {@code beginIndex} to the end of the array.
     * @param beginIndex The nonnegative starting index
     * @return A string with joined arguments, or empty if {@code beginIndex >= ctx.args.length}
     * @throws IllegalArgumentException If {@code beginIndex < 0}
     */
    public @NonNull String joinArgsSlice(int beginIndex) {
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
    public @NonNull String joinArgsSlice(int beginIndex, int endIndex) {
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

    /**
     * @return Whether menus are enabled
     */
    public abstract boolean shouldUseMenus();

    /**
     * Gets a localized string for the current lang.
     * @param key The <b>case-sensitive</b> localization key
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull String i18n(@NonNull String key) {
        return getCmd().i18n(getLang(), key);
    }
    /**
     * Gets a localized, formatted string for the current lang.
     * @param key The <b>case-sensitive</b> localization key
     * @param args An ordered list of arguments to place into the string
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull String i18nf(@NonNull String key, Object... args) {
        return getCmd().i18nf(getLang(), key, args);
    }

    /**
     * Logs a message to the logging channel.
     */
    public abstract void log(@NonNull String m);
    /**
     * Logs a message to the logging channel.
     */
    public abstract void log(@NonNull Message m);
    /**
     * Logs a message to the logging channel.
     */
    public abstract void log(@NonNull MessageEmbed m);

    /**
     * Handles an uncaught exception thrown by a command by replying to the user.
     * Users should <b>never</b> see this, even if an external API messed up.
     * Treat all uncaught exceptions as programming failures.
     * @param ex The exception that was thrown
     */
    public void handleException(Throwable ex) {
        try {
            log.error("Uncaught exception for command execution " + this, ex);
            String unexpected = "There was an unexpected exception: " + MarkdownUtil.monospace(ex.toString());
            String errorMessage = Result.EXCEPTION.addEmote(unexpected);
            if (getConfig().getFlagConfig().isDebugMode()) {
                errorMessage += buildStackTrace(ex);
                // Not guaranteed to escape properly, but since users should never see exceptions, it's not necessary
                if (errorMessage.length() >= Message.MAX_CONTENT_LENGTH) {
                    errorMessage = errorMessage.substring(0, Message.MAX_CONTENT_LENGTH - 3) + "```";
                }
            }
            sendMessage(errorMessage);
            log(errorMessage);
        } catch (Exception ex2) {
            log.error("Somehow, there was an exception processing an uncaught exception", ex2);
        } finally {
            commandResult(Result.EXCEPTION);
        }
    }
    private static String buildStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append(ste);
            String className = ste.getClassName();
            if (className.contains("net.dv8tion") || className.contains("com.neovisionaries")) {
                sb.append("...");
                break;
            }
            sb.append("\n");
        }
        if (sb.charAt(sb.length() - 1) == '\n') {
            sb.setLength(sb.length() - 1);
        }
        return MarkdownUtil.codeblock(sb.toString());
    }

    /**
     * Creates a new callback builder that automatically replies if an uncaught exception is thrown.
     * @param future The future to add callbacks to
     * @param <T> The type of the future
     * @return A callback builder with a preset uncaught function
     */
    public <T> FutureCallback.Builder<T> newCallbackBuilder(CompletableFuture<T> future) {
        return FutureCallback.builder(future).onUncaught(this::handleException);
    }

    /**
     * @return The user ID
     */
    public long getUserId() {
        return getE().getAuthor().getIdLong();
    }

    /**
     * Shortcut for {@link #getBot}.{@link Bot#getMCLibrary() getMCLibrary()}
     * @return The Minecraft library object
     */
    public @NonNull MCLibrary getMCLibrary() {
        return getBot().getMCLibrary();
    }

    /**
     * Shortcut for {@link #getBot}.{@link Bot#getDatabaseCache() getDatabase()}
     * @return The guild, channel, and user cache associated with this bot
     */
    public @NonNull DatabaseCache getCache() {
        return getBot().getDatabaseCache();
    }

    /**
     * Gets a guild from the backend
     * @param gid The guild id
     * @return Either the cached guild or one with default settings
     */
    public @NonNull DbGuild getGuild(long gid) {
        return getCache().getGuild(gid);
    }
    /**
     * Gets a guild from the backend
     * @param g The JDA guild object
     * @return Either the cached guild or one with default settings
     */
    public @NonNull DbGuild getGuild(@NonNull Guild g) {
        return getGuild(g.getIdLong());
    }

    /**
     * Gets a channel from the backend
     * <br>If the guild id is known, use {@link #getChannel(long, long)}.
     * @param cid The channel id
     * @return Either the cached channel, or empty since the guild ID must be known to create a new channel
     */
    public Optional<DbChannel> getChannel(long cid) {
        return getCache().getChannel(cid);
    }
    /**
     * Gets a channel from the backend
     * @param cid The channel id
     * @param gid The guild id
     * @return Either the cached channel or one with default settings
     */
    public @NonNull DbChannel getChannel(long cid, long gid) {
        return getCache().getChannel(cid, gid);
    }
    /**
     * Gets a channel from the backend
     * @param c The channel
     * @return Either the cached channel or one with default settings
     */
    public @NonNull DbChannel getChannel(@NonNull TextChannel c) {
        return getChannel(c.getIdLong(), c.getGuild().getIdLong());
    }
    /**
     * Gets all channels in the database that have the specified guild id.
     * @param gid The guild id
     * @return A possibly-empty list of channels
     */
    public List<DbChannel> getChannelsInGuild(long gid) {
        return getCache().getChannelsInGuild(gid);
    }

    /**
     * Gets a user from the backend
     * @param uid The user id
     * @return Either the cached user or one with default settings
     */
    public @NonNull DbUser getUser(long uid) {
        return getCache().getUser(uid);
    }
    /**
     * Gets a user from the backend
     * @param u The JDA user object
     * @return Either the cached user or one with default settings
     */
    public @NonNull DbUser getUser(@NonNull User u) {
        return getUser(u.getIdLong());
    }

}
