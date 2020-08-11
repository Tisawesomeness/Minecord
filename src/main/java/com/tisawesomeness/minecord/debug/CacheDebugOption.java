package com.tisawesomeness.minecord.debug;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;

/**
 * Debugs a Caffeine {@link com.github.benmanes.caffeine.cache.Cache}.
 */
public abstract class CacheDebugOption implements DebugOption {

    public static final int MILLION = 1_000_000;

    public @NonNull String debug() {
        CacheStats stats = getCacheStats();
        return String.format("**%s Stats**\n", getName()) +
                String.format("Hits: `%s/%s %.2f%%`\n", stats.hitCount(), stats.requestCount(), 100*stats.hitRate()) +
                String.format("Load Failures: `%s/%s %.2f%%`\n", stats.loadFailureCount(), stats.loadCount(), 100*stats.loadFailureRate()) +
                String.format("Eviction Count: `%s`\n", stats.evictionCount()) +
                String.format("Average Load Penalty: `%.3fms`\n", stats.averageLoadPenalty() / MILLION) +
                String.format("Total Load Time: `%sms`", stats.totalLoadTime() / MILLION);
    }
    /**
     * @return The cache stats to be used in {@link #debug()}.
     */
    public abstract @NonNull CacheStats getCacheStats();
}
