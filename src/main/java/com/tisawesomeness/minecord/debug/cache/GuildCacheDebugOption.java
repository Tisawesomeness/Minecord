package com.tisawesomeness.minecord.debug.cache;

import com.tisawesomeness.minecord.database.DatabaseCache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GuildCacheDebugOption extends CacheDebugOption {
    private final @NonNull DatabaseCache cache;
    public Optional<CacheStats> getCacheStats(@NonNull String extra) {
        return Optional.of(cache.getGuildStats());
    }
    public @NonNull String getName() {
        return "guildCache";
    }
}
