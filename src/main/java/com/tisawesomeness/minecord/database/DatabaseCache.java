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

public class DatabaseCache {

    private final Database db;

    private final LoadingCache<Long, Optional<DbGuild>> guilds;
    private final LoadingCache<Long, Optional<DbChannel>> channels;
    private final LoadingCache<Long, Optional<DbUser>> users;

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
    private static <T, R> LoadingCache<T, R> build(CacheBuilder<Object, Object> builder, ThrowingFunction<T, R> loadFunction) {
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
     * <br>The guild is cleared from the cache once it is altered.
     * @param id The guild id.
     * @return The guild if present, or an empty Optional if not present in the database or an exception occured.
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
     * Either gets a guild from the cache or queries the database for it.
     * <br>The guild is cleared from the cache once it is altered.
     * @param id The guild id.
     * @return The guild if present, or an empty Optional if not present in the database or an exception occured.
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
     * <br>The user is cleared from the cache once it is altered.
     * @param id The user id.
     * @return The user if present, or an empty Optional if not present in the database or an exception occured.
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

    public void invalidateGuild(long id) {
        guilds.invalidate(id);
    }
    public void invalidateChannel(long id) {
        channels.invalidate(id);
    }
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
