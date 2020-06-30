package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.database.Database;

import com.google.common.cache.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuildCacheDebugOption extends CacheDebugOption {
    private final @NonNull Database db;
    public @NonNull CacheStats getCacheStats() {
        return db.getGuildStats();
    }
    public @NonNull String getName() {
        return "guildCache";
    }
}
