package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Lang;
import com.tisawesomeness.minecord.command.misc.HelpCommand;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Stores the information available to every command
 */
@RequiredArgsConstructor
public class CommandContext {
    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    public final @NonNull String[] args;
    /**
     * The event that triggered the command.
     */
    public final @NonNull MessageReceivedEvent e;
    /**
     * The loaded config.
     */
    public final @NonNull Config config;
    /**
     * A link to the bot instance.
     */
    public final @NonNull Bot bot;
    /**
     * The original command that created this context
     */
    public final @NonNull Command cmd;
    /**
     * The executor in charge of running this command and tracking cooldowns
     */
    public final @NonNull CommandExecutor executor;
    /**
     * Whether the user executing the command is elevated.
     */
    public final boolean isElevated;
    // These settings are used often and calculated beforehand, so simply passing their values makes sense
    /**
     * The current prefix.
     */
    public final @NonNull String prefix;
    /**
     * The current language. Use {@link #i18n(String)} as a shortcut for {@link Lang#i18n(String) lang.get(String)}.
     */
    public final @NonNull Lang lang;
    /**
     * The current locale, used in formatters.
     */
    public final @NonNull Locale locale;
    // These settings are only used occasionally, it's best to pass the setting and evaluate when needed
    private final @NonNull UseMenusSetting useMenusSetting;

    public CommandContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Config config,
                          @NonNull Bot bot, @NonNull Command cmd, @NonNull CommandExecutor executor, boolean isElevated,
                          @NonNull String prefix, @NonNull Lang lang) {
        this.args = args;
        this.e = e;
        this.config = config;
        this.bot = bot;
        this.cmd = cmd;
        this.executor = executor;
        this.isElevated = isElevated;
        this.prefix = prefix;
        this.lang = lang;
        locale = lang.getLocale();
        useMenusSetting = bot.getSettings().useMenus;
    }

    /**
     * Equivalent to {@code String.join(" ", ctx.args)}
     * @return All arguments as a single string
     */
    public String joinArgs() {
        return String.join(" ", args);
    }
    /**
     * Creates a string with arguments from {@code beginIndex} to the end of the array.
     * @param beginIndex The positive starting index
     * @return A string with joined arguments, or empty if {@code beginIndex >= ctx.args.length}.
     */
    public String joinArgsSlice(int beginIndex) {
        return Arrays.stream(args)
                .skip(beginIndex)
                .collect(Collectors.joining(" "));
    }
    /**
     * Creates a string with arguments from {@code beginIndex} to {@code endIndex - 1}.
     * @param beginIndex The positive starting index
     * @param endIndex The ending index, must be greater than or equal to {@code beginIndex}, may be out of bounds
     * @return A string with joined arguments, or empty if {@code beginIndex >= ctx.args.length}.
     */
    public String joinArgsSlice(int beginIndex, int endIndex) {
        return Arrays.stream(args)
                .skip(beginIndex)
                .limit(endIndex - beginIndex)
                .collect(Collectors.joining(" "));
    }

    /**
     * Shortcut for {@link #bot}.{@link Bot#getDatabaseCache() getDatabase()}
     * @return The guild, channel, and user cache associated with this bot
     */
    public DatabaseCache getCache() {
        return bot.getDatabaseCache();
    }

    /**
     * Gets a guild from the backend
     * @param gid The guild id
     * @return Either the cached guild or one with default settings
     */
    public DbGuild getGuild(long gid) {
        return getCache().getGuild(gid);
    }
    /**
     * Gets a guild from the backend
     * @param g The JDA guild object
     * @return Either the cached guild or one with default settings
     */
    public DbGuild getGuild(Guild g) {
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
    public DbChannel getChannel(long cid, long gid) {
        return getCache().getChannel(cid, gid);
    }
    /**
     * Gets a channel from the backend
     * @param c The channel
     * @return Either the cached channel or one with default settings
     */
    public DbChannel getChannel(TextChannel c) {
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
    public DbUser getUser(long uid) {
        return getCache().getUser(uid);
    }
    /**
     * Gets a user from the backend
     * @param u The JDA user object
     * @return Either the cached user or one with default settings
     */
    public DbUser getUser(User u) {
        return getUser(u.getIdLong());
    }

    public boolean shouldUseMenus() {
        return useMenusSetting.getEffective(this);
    }

    /**
     * Gets a locallized string for the current lang.
     * @param key The <b>case-sensitive</b> localization key
     * @return The localized string
     * @throws java.util.MissingResourceException If the given key could not be found.
     * @see MessageFormat
     * @see Locale
     */
    public @NonNull String i18n(String key) {
        return cmd.i18n(lang, key);
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
    public @NonNull String i18nf(String key, Object... args) {
        return cmd.i18nf(lang, key, args);
    }

    /**
     * Logs a message to the logging channel.
     */
    public void log(String m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(Message m) {
        bot.log(m);
    }
    /**
     * Logs a message to the logging channel.
     */
    public void log(MessageEmbed m) {
        bot.log(m);
    }

    /**
     * Adds the footer with the rolled announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added footer.
     */
    public EmbedBuilder addFooter(EmbedBuilder eb) {
        if (config.getFlagConfig().isUseAnnouncements()) {
            return eb.setFooter(bot.getAnnounceRegistry().roll(bot.getShardManager()));
        }
        User author = e.getAuthor();
        String requestedBy = "Requested by " + author.getAsTag();
        return eb.setFooter(requestedBy, author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // TODO temporarily disabled (change to static image)
//        if (Config.getOwner().equals("0")) {
//            return eb.setFooter(announcement);
//        }
//        User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
//        return eb.setFooter(announcement, owner.getAvatarUrl());
    }

    /**
     * Checks if the user has all permissions in the current channel.
     * @param permissions A list of permissions
     * @return True if the user is elevated or has all permissions in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public boolean userHasPermission(Permission... permissions) {
        if (isElevated) {
            return true;
        }
        if (!e.isFromGuild()) {
            throw new IllegalStateException("Permisssions can only be checked in commands sent from guilds.");
        }
        return Objects.requireNonNull(e.getMember()).hasPermission(e.getTextChannel(), permissions);
    }
    /**
     * Checks if the bot has all permissions in the current channel.
     * <br><b>Do not assume the bot has every permission requested in the invite.</b>
     * @param permissions A list of permissions
     * @return True only if the bot has every permission in the list
     * @throws IllegalStateException If the command was executed in DMs
     */
    public boolean botHasPermission(Permission... permissions) {
        return e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), permissions);
    }

    /**
     * Replies to the sender of this command.
     * @param text The text to send. Must be shorter than {@link Message#MAX_CONTENT_LENGTH}.
     * @return Success
     */
    public Result reply(CharSequence text) {
        e.getChannel().sendMessage(text).queue();
        return Result.SUCCESS;
    }
    /**
     * Replies to the sender of this command with an embed.
     * <br>The embed is sent with no modifications.
     * @param eb The embed builder to be built and sent. Must satisfy all ebmed length limits.
     * @return Success
     */
    public Result replyRaw(EmbedBuilder eb) {
        e.getChannel().sendMessage(eb.build()).queue();
        return Result.SUCCESS;
    }
    /**
     * Replies to the sender of this command with an embed.
     * <br>The bot color and a random announcement (if enabled) are added.
     * @param eb The embed builder to be built and sent. Must satisfy all ebmed length limits.
     * @return Success
     */
    public Result reply(EmbedBuilder eb) {
        return replyRaw(brand(eb));
    }

    /**
     * Tells the sender of this command that they need to be elevated.
     * <br>The not elevated emote is added to the message.
     * @param text The warning message
     * @return Not elevated permissions result
     */
    public Result notElevated(CharSequence text) {
        return sendResult(Result.NOT_ELEVATED, text);
    }
    /**
     * Tells the sender of this command that they do not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     * @return No user permissions result
     */
    public Result noUserPermissions(CharSequence text) {
        return sendResult(Result.NO_USER_PERMISSIONS, text);
    }
    /**
     * Tells the sender of this command that the bot does not have the correct permissions.
     * <br>The no permissions emote is added to the message.
     * @param text The warning message
     * @return No bot permissions result
     */
    public Result noBotPermissions(CharSequence text) {
        return sendResult(Result.NO_BOT_PERMISSIONS, text);
    }
    /**
     * Tells the sender of this command they used it wrong.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Invalid args result
     */
    public Result invalidArgs(CharSequence text) {
        return sendResult(Result.INVALID_ARGS, text);
    }
    /**
     * Warns the sender of this command.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Warning
     */
    public Result warn(CharSequence text) {
        return sendResult(Result.WARNING, text);
    }
    /**
     * Warns the sender of this command, but keeps in mind the possibility it may actually be an error.
     * <br>The warning emote is added to the message.
     * @param text The warning message
     * @return Possible Error
     */
    public Result possibleErr(CharSequence text) {
        return sendResult(Result.POSSIBLE_ERROR, text);
    }
    /**
     * Displays an error to the sender of this command.
     * <br>The error emote is added to the message.
     * @param text The error message
     * @return Error
     */
    public Result err(CharSequence text) {
        return sendResult(Result.ERROR, text);
    }

    /**
     * Sends a message to the current channel, adding the appropiate emote.
     * @param result The result to get the emote from
     * @param text The message to send
     * @return The given result
     */
    public Result sendResult(Result result, CharSequence text) {
        String msg = result.addEmote(text, lang);
        e.getChannel().sendMessage(msg).queue();
        return result;
    }

    /**
     * Creates an embed with the current command's help.
     * @return The Help result
     */
    public Result showHelp() {
        reply(HelpCommand.showHelp(this, cmd));
        return Result.HELP;
    }
    /**
     * Adds the bot color and a random announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added branding.
     */
    public EmbedBuilder brand(EmbedBuilder eb) {
        return addFooter(eb).setColor(Bot.color);
    }

    /**
     * Starts the cooldown timer for this command, unless the user is elevated and skipping cooldowns is enabled.
     */
    public void triggerCooldown() {
        if (!executor.shouldSkipCooldown(this)) {
            executor.startCooldown(cmd, e.getAuthor().getIdLong());
        }
    }

}
