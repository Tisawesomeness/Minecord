package com.tisawesomeness.minecord.debug.cache;

import com.tisawesomeness.minecord.mc.external.DualPlayerProvider;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class PlayerCacheDebugOption extends CacheDebugOption {

    private final @Nullable DualPlayerProvider playerProvider;
    public PlayerCacheDebugOption(PlayerProvider playerProvider) {
        if (playerProvider instanceof DualPlayerProvider) {
            this.playerProvider = (DualPlayerProvider) playerProvider;
        } else {
            this.playerProvider = null;
        }
    }

    public Optional<CacheStats> getCacheStats(@NonNull String extra) {
        return Optional.ofNullable(playerProvider).map(DualPlayerProvider::getPlayerCacheStats);
    }
    public @NonNull String getName() {
        return "playerCache";
    }

}
