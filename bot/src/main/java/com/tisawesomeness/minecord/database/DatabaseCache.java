package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.config.config.CacheConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.database.dao.DbChannel;
import com.tisawesomeness.minecord.database.dao.DbGuild;
import com.tisawesomeness.minecord.database.dao.DbUser;
import com.tisawesomeness.minecord.util.type.ThrowingFunction;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.Cleanup;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles cached values from the database in order to minimize creating expensive database connections
 */
public class DatabaseCache {

    private final Database db;

    private final LoadingCache<Long, DbGuild> guilds;
    private final LoadingCache<Long, DbChannel> channels;
    private final LoadingCache<Long, DbUser> users;

    /**
     * Sets up the cache to mirror database tables.
     * @param db The database used to load values from
     * @param config If debugMode is true, cache stats are recorded (at the cost of performance)
     */
    public DatabaseCache(Database db, Config config) {
        this.db = db;
        guilds = build(builder(config, CacheType.GUILD), key -> DbGuild.load(db, key));
        channels = build(builder(config, CacheType.CHANNEL), key -> DbChannel.load(db, key));
        users = build(builder(config, CacheType.USER), key -> DbUser.load(db, key));
    }

    private static Caffeine<Object, Object> builder(Config config, CacheType type) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(getLifetime(config, type)));
        int maxSize = getMaxSize(config, type);
        if (maxSize >= 0) {
            builder.maximumSize(maxSize);
        }
        if (config.getFlagConfig().isDebugMode()) {
            builder.recordStats();
        }
        return builder;
    }
    private static int getLifetime(Config config, CacheType type) {
        CacheConfig cc = config.getAdvancedConfig().getDatabaseCacheConfig();
        switch (type) {
            case GUILD:
                return cc.getGuildLifetime();
            case CHANNEL:
                return cc.getChannelLifetime();
            case USER:
                return cc.getUserLifetime();
        }
        throw new AssertionError("Unreachable");
    }
    private static int getMaxSize(Config config, CacheType type) {
        CacheConfig cc = config.getAdvancedConfig().getDatabaseCacheConfig();
        switch (type) {
            case GUILD:
                return cc.getGuildMaxSize();
            case CHANNEL:
                return cc.getChannelMaxSize();
            case USER:
                return cc.getUserMaxSize();
        }
        throw new AssertionError("Unreachable");
    }
    // Shortens code by matching loadFunction type to cache type, getting rid of the explicit CacheLoader declaration
    private static <T, R> LoadingCache<T, R> build(
            Caffeine<Object, Object> builder, ThrowingFunction<? super T, ? extends R, SQLException> loadFunction) {
        return builder.build(loadFunction::apply);
    }

    /**
     * Either gets a guild from the cache or queries the database for it.
     * <br>If the guild id is known, use {@link #getChannel(long, long)}.
     * @param id The guild id
     * @return The guild if present, or a new default guild
     */
    public @NonNull DbGuild getGuild(long id) {
        DbGuild guild = guilds.get(id);
        if (guild == null) {
            return new DbGuild(db, id);
        }
        return guild;
    }

    /**
     * Either gets a channel from the cache or queries the database for it.
     * If the guild
     * @param id The channel id
     * @return The channel if present, or an empty Optional if not present in the database or an exception occured
     */
    public Optional<DbChannel> getChannel(long id) {
        return Optional.ofNullable(channels.get(id));
    }
    /**
     * Either gets a channel from the cache or queries the database for it.
     * @param id The channel id
     * @param guildId The guild id, which is necessary for "get all channels in guild" queries
     * @return The channel if present, or a new default channel
     */
    public @NonNull DbChannel getChannel(long id, long guildId) {
        DbChannel channel = channels.get(id);
        if (channel == null) {
            return new DbChannel(db, id, guildId);
        }
        return channel;
    }
    /**
     * Gets all channels in the database that have the specified guild id.
     * @param id The guild id
     * @return A possibly-empty list of channels
     */
    public List<DbChannel> getChannelsInGuild(long id) {
        try {
            @Cleanup Connection connect = db.getConnect();
            @Cleanup PreparedStatement st = connect.prepareStatement("SELECT * FROM channel WHERE guild_id = ?;");
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            List<DbChannel> channelList = new ArrayList<>();
            while (rs.next()) {
                // Placing channels directly in cache to speed up later requests
                DbChannel channel = DbChannel.from(db, rs);
                channels.put(channel.getId(), channel);
                channelList.add(channel);
            }
            return Collections.unmodifiableList(channelList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Either gets a user from the cache or queries the database for it.
     * @param id The user id
     * @return The user if present, or a new default user
     */
    public DbUser getUser(long id) {
        DbUser user = users.get(id);
        if (user == null) {
            return new DbUser(db, id);
        }
        return user;
    }

    /**
     * Marks a guild as no longer valid, meaning the next {@link #getGuild(long)} operation
     * will grab an updated value from the database.
     * @param id The guild id
     */
    public void invalidateGuild(long id) {
        guilds.invalidate(id);
    }
    /**
     * Marks a channel as no longer valid, meaning the next {@link #getChannel(long, long)} operation
     * will grab an updated value from the database.
     * @param id The channel id
     */
    public void invalidateChannel(long id) {
        channels.invalidate(id);
    }
    /**
     * Marks a user as no longer valid, meaning the next {@link #getUser(long)} operation
     * will grab an updated value from the database.
     * @param id The user id
     */
    public void invalidateUser(long id) {
        users.invalidate(id);
    }

    public CacheStats getGuildStats() {
        return guilds.stats();
    }
    public CacheStats getChannelStats() {
        return channels.stats();
    }
    public CacheStats getUserStats() {
        return users.stats();
    }

    private enum CacheType {
        GUILD, CHANNEL, USER
    }

}
