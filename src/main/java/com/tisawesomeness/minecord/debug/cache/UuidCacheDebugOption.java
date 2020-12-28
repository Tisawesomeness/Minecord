package com.tisawesomeness.minecord.debug.cache;

import com.tisawesomeness.minecord.mc.player.PlayerProvider;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UuidCacheDebugOption extends CacheDebugOption {
    private final @NonNull PlayerProvider playerProvider;
    public Optional<CacheStats> getCacheStats(@NonNull String extra) {
        return Optional.of(playerProvider.getUuidCacheStats());
    }
    public @NonNull String getName() {
        return "uuidCache";
    }
}
