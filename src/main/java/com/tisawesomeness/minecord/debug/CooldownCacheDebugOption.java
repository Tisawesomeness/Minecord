package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.command.CommandExecutor;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CooldownCacheDebugOption extends CacheDebugOption {
    private final @NonNull CommandExecutor executor;
    public @NonNull String getName() {
        return "cooldownCache";
    }
    public @NonNull Optional<CacheStats> getCacheStats(@NonNull String extra) {
        if (extra.isEmpty()) {
            return Optional.of(executor.stats());
        }
        return executor.stats(extra);
    }
}
