package com.tisawesomeness.minecord.debug.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.tisawesomeness.minecord.command.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CooldownCacheDebugOption extends CacheDebugOption {
    public @NonNull String getName() {
        return "cooldownCache";
    }
    public Optional<CacheStats> getCacheStats(@NonNull String extra) {
        if (extra.isEmpty()) {
            return Optional.of(Registry.cooldownStats());
        }
        return Registry.cooldownStats(extra);
    }
}
