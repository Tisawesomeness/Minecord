package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.command.CommandExecutor;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CooldownCacheDebugOption extends CacheDebugOption {
    private final @NonNull CommandExecutor executor;
    public @NonNull String getName() {
        return "cooldownCache";
    }
    public @NonNull CacheStats getCacheStats() {
        return executor.stats();
    }
}
