package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.database.DatabaseCache;

import com.google.common.cache.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChannelCacheDebugOption extends CacheDebugOption {
    private final @NonNull DatabaseCache cache;
    public @NonNull CacheStats getCacheStats() {
        return cache.getChannelStats();
    }
    public @NonNull String getName() {
        return "channelCache";
    }
}
