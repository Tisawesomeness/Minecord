package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Bot;
import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.database.Database;
import com.tisawesomeness.minecord.database.DatabaseCache;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.setting.SettingRegistry;
import com.tisawesomeness.minecord.setting.impl.UseMenusSetting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Stores the information available to every command
 */
@RequiredArgsConstructor
public class CommandContext {
    /**
     * The arguments given to the command, split by spaces. May be length 0.
     */
    @With
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
     * Whether the user executing the command is elevated.
     */
    public final boolean isElevated;
    /**
     * The current prefix. Either guild-specific or the config default for DMs.
     */
    public final @NonNull String prefix;

    public CommandContext(@NonNull String[] args, @NonNull MessageReceivedEvent e, @NonNull Bot bot,
                          @NonNull Config config, boolean isElevated, @NonNull String prefix,
                          @NonNull SettingRegistry settings) {
        this(args, e, config, bot, isElevated, prefix, settings.useMenus);
    }

    /**
     * Shortcut for {@link #bot}.{@link Bot#getDatabase() getDatabase()}.{@link Database#getCache() getCache()}
     * @return The guild, channel, and user cache associated with this bot
     */
    public DatabaseCache getCache() {
        return bot.getDatabase().getCache();
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

    private final @NonNull UseMenusSetting useMenusSetting;
    public boolean shouldUseMenus() {
        return useMenusSetting.getEffective(this);
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
     * Formats a message with URL to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header of the message.
     * @param body The main body or description of the message.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedMessage(@Nullable String title, @Nullable CharSequence body) {
        return brand(new EmbedBuilder()
                .setTitle(title)
                .setDescription(body));
    }

    /**
     * Formats a message with URL to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header of the message.
     * @param url A URL that the title goes to when clicked.
     * @param body The main body or description of the message.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedURL(@Nullable String title, @Nullable String url, @Nullable CharSequence body) {
        return brand(new EmbedBuilder()
                .setTitle(title, url)
                .setDescription(body));
    }

    /**
     * Formats an image to look more fancy using an embed.
     * Pass {@code null} in any argument to remove that part of the message.
     * @param title The title or header.
     * @param url The URL of the image.
     * @return The EmbedBuilder with the added info and bot branding.
     */
    public EmbedBuilder embedImage(@Nullable String title, @Nullable String url) {
        return brand(new EmbedBuilder()
                .setTitle(title)
                .setImage(url));
    }

    /**
     * Adds the footer with the rolled announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added footer.
     */
    public EmbedBuilder addFooter(EmbedBuilder eb) {
        if (config.useAnnouncements) {
            return eb.setFooter(bot.getAnnounceRegistry().roll(bot.getShardManager()));
        }
        User author = e.getAuthor();
        String requestedBy = "Requested by " + author.getAsTag();
        return eb.setFooter(requestedBy, author.getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        // TODO temporarily disabled (change to static image)
//		if (Config.getOwner().equals("0")) {
//			return eb.setFooter(announcement);
//		}
//		User owner = Bot.shardManager.retrieveUserById(Config.getOwner()).complete();
//		return eb.setFooter(announcement, owner.getAvatarUrl());
    }

    /**
     * Adds the bot color and a random announcement to an embed.
     * @param eb The given EmbedBuilder.
     * @return The same builder with added branding.
     */
    public EmbedBuilder brand(EmbedBuilder eb) {
        return addFooter(eb).setColor(Bot.color);
    }

}
