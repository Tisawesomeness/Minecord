package com.tisawesomeness.minecord.debug.cache;

import com.tisawesomeness.minecord.command.CommandExecutor;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class LinkCacheDebugOption extends CacheDebugOption {
    private final @NonNull CommandExecutor executor;
    public @NonNull String getName() {
        return "linkCache";
    }
    public Optional<CacheStats> getCacheStats(@NonNull String extra) {
        return executor.linkStats();
    }
}
