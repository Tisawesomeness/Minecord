package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.database.Database;

import com.google.common.cache.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCacheDebugOption extends CacheDebugOption {
    private final @NonNull Database db;
    public @NonNull CacheStats getCacheStats() {
        return db.getUserStats();
    }
    public @NonNull String getName() {
        return "userCache";
    }
}
