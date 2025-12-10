package com.tisawesomeness.minecord.debug.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.tisawesomeness.minecord.debug.DebugOption;
import lombok.NonNull;

import java.util.Optional;

/**
 * Debugs a Caffeine {@link com.github.benmanes.caffeine.cache.Cache}.
 */
public abstract class CacheDebugOption implements DebugOption {

    public static final int MILLION = 1_000_000;

    public @NonNull String debug(@NonNull String extra) {
        Optional<CacheStats> statsOpt = getCacheStats(extra);
        if (!statsOpt.isPresent()) {
            return "N/A";
        }
        CacheStats stats = statsOpt.get();
        return String.format("**%s Stats**\n", getName()) +
                String.format("Hits: `%s/%s %.2f%%`\n", stats.hitCount(), stats.requestCount(), 100*stats.hitRate()) +
                String.format("Load Failures: `%s/%s %.2f%%`\n", stats.loadFailureCount(), stats.loadCount(), 100*stats.loadFailureRate()) +
                String.format("Eviction Count: `%s`\n", stats.evictionCount()) +
                String.format("Average Load Penalty: `%.3fms`\n", stats.averageLoadPenalty() / MILLION) +
                String.format("Total Load Time: `%sms`", stats.totalLoadTime() / MILLION);
    }
    /**
     * @return The cache stats to be used in {@link #debug(String)}.
     */
    public abstract Optional<CacheStats> getCacheStats(@NonNull String extra);
}
