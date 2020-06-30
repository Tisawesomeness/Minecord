package com.tisawesomeness.minecord.debug;

import com.google.common.cache.CacheStats;
import lombok.NonNull;

/**
 * Debugs a Guava {@link com.google.common.cache.LoadingCache}.
 */
public abstract class CacheDebugOption implements DebugOption {
    public @NonNull String debug() {
        CacheStats stats = getCacheStats();
        return String.format("**%s Stats**\n", getName()) +
                String.format("Hits: `%s/%s %.2f%%`\n", stats.hitCount(), stats.requestCount(), stats.hitRate()) +
                String.format("Load Exceptions: `%s/%s %.2f%%`\n", stats.loadExceptionCount(), stats.loadCount(), 100*stats.loadExceptionRate()) +
                String.format("Eviction Count: `%s`\n", stats.evictionCount()) +
                String.format("Average Load Penalty: `%.3fms`", stats.averageLoadPenalty() / 1_000_000);
    }
    /**
     * @return The cache stats to be used in {@link #debug()}.
     */
    public abstract @NonNull CacheStats getCacheStats();
}
