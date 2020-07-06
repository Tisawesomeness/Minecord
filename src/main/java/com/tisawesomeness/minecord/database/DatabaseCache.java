package com.tisawesomeness.minecord.database;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.type.ThrowingFunction;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Handles cached values from the database in order to minimize creating expensive database connections
 */
public class DatabaseCache {

    private final Database db;

    private final LoadingCache<Long, Optional<DbGuild>> guilds;
    private final LoadingCache<Long, Optional<DbChannel>> channels;
    private final LoadingCache<Long, Optional<DbUser>> users;

    /**
     * Sets up the cache to mirror database tables.
     * @param db The database used to load values from
     * @param config If debugMode is true, cache stats are recorded (at the cost of performance)
     */
    public DatabaseCache(Database db, Config config) {
        this.db = db;
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES);
        if (config.debugMode) {
            builder.recordStats();
        }
        guilds = build(builder, key -> DbGuild.load(db, key));
        channels = build(builder, key -> DbChannel.load(db, key));
        users = build(builder, key -> DbUser.load(db, key));
    }

    /**
     * Helper function to build caches without repetitive code.
     * @param builder The cache builder to build from, which stays unmodified.
     * @param loadFunction A reference to a defined function with {@code T} as the input and {@code U} as the output.
     * @param <T> The type of the cache key.
     * @param <R> The type of the cache value.
     * @return A Guava cache with the specified loading function.
     */
    private static <T, R> LoadingCache<T, R> build(
            CacheBuilder<Object, Object> builder, ThrowingFunction<? super T, ? extends R> loadFunction) {
        return builder.build(new CacheLoader<T, R>() {
            @Override
            // @NotNull used to satisfy warning
            public @NonNull R load(@NotNull T key) {
                return loadFunction.apply(key);
            }
        });
    }

    /**
     * Either gets a guild from the cache or queries the database for it.
     * @param id The guild id
     * @return The guild if present, or an empty Optional if not present in the database or an exception occured
     */
    public DbGuild getGuild(long id) {
        try {
            Optional<DbGuild> guildOpt = guilds.get(id);
            if (guildOpt.isPresent()) {
                return guildOpt.get();
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return new DbGuild(db, id);
    }
    /**
     * Either gets a channel from the cache or queries the database for it.
     * @param id The channel id
     * @param guildId The guild id, which is necessary for "get all channels in guild" queries
     * @return The channel if present, or an empty Optional if not present in the database or an exception occured
     */
    public DbChannel getChannel(long id, long guildId) {
        try {
            Optional<DbChannel> channelOpt = channels.get(id);
            if (channelOpt.isPresent()) {
                return channelOpt.get();
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return new DbChannel(db, id, guildId);
    }
    /**
     * Either gets a user from the cache or queries the database for it.
     * @param id The user id
     * @return The user if present, or an empty Optional if not present in the database or an exception occured
     */
    public DbUser getUser(long id) {
        try {
            Optional<DbUser> userOpt = users.get(id);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
        return new DbUser(db, id);
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

}
