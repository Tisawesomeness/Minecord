package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.database.Database;

import com.google.common.cache.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChannelCacheDebugOption extends CacheDebugOption {
    private final @NonNull Database db;
    public @NonNull CacheStats getCacheStats() {
        return db.getChannelStats();
    }
    public @NonNull String getName() {
        return "channelCache";
    }
}
