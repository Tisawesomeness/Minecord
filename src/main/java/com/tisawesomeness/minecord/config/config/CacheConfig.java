package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Configures the behavior of various caches.
 */
@Value
public class CacheConfig {

    @JsonProperty("guildLifetime")
    int guildLifetime;
    @JsonProperty("guildMaxSize")
    int guildMaxSize;
    @JsonProperty("channelLifetime")
    int channelLifetime;
    @JsonProperty("channelMaxSize")
    int channelMaxSize;
    @JsonProperty("userLifetime")
    int userLifetime;
    @JsonProperty("userMaxSize")
    int userMaxSize;

    @JsonProperty("cooldownTolerance")
    double cooldownTolerance;
    @JsonProperty("linkLifetime")
    int linkLifetime;
    @JsonProperty("linkMaxSize")
    int linkMaxSize;

    @JsonProperty("mojangUuidLifetime")
    int mojangUuidLifetime;
    @JsonProperty("mojangPlayerLifetime")
    int mojangPlayerLifetime;
    @JsonProperty("gappleStatusLifetime")
    int gappleStatusLifetime;

    /**
     * Verifies that all cache values are valid
     * @return The Verification
     */
    public Verification verify(boolean useGappleAPI) {
        return Verification.combineAll(
                verifyCacheLifetime(guildLifetime, "Guild"),
                verifyCacheMaxSize(guildMaxSize, "Guild"),
                verifyCacheLifetime(channelLifetime, "Channel"),
                verifyCacheMaxSize(channelMaxSize, "Channel"),
                verifyCacheLifetime(userLifetime, "User"),
                verifyCacheMaxSize(userMaxSize, "User"),
                verifyCooldownTolerance(),
                verifyCacheLifetime(linkLifetime, "Link"),
                verifyCacheMaxSize(linkMaxSize, "Link"),
                verifyUuid(),
                verifyPlayer(),
                verifyGappleCache(useGappleAPI)
        );
    }

    private static Verification verifyCacheLifetime(long lifetime, String cacheName) {
        return Verification.verify(lifetime >= 0, cacheName + " lifetime cannot be negative");
    }
    private static Verification verifyCacheMaxSize(long maxSize, String cacheName) {
        return Verification.verify(maxSize >= -1, cacheName + " cache max size must be -1, 0, or positive");
    }

    private Verification verifyCooldownTolerance() {
        if (cooldownTolerance >= 0.0) {
            return Verification.valid();
        }
        return Verification.invalid("Cooldown tolerance cannot be negative");
    }

    private Verification verifyUuid() {
        if (mojangUuidLifetime >= MojangAPI.PROFILE_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid(
                "UUIDs must be in the cache for at least " + MojangAPI.PROFILE_RATELIMIT + " seconds");
    }
    private Verification verifyPlayer() {
        if (mojangUuidLifetime >= MojangAPI.PROFILE_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid(
                "Players must be in the cache for at least " + MojangAPI.PROFILE_RATELIMIT + " seconds");
    }

    private Verification verifyGappleCache(boolean useGappleAPI) {
        if (!useGappleAPI) {
            return Verification.valid();
        }
        return verifyCacheLifetime(gappleStatusLifetime, "Gapple status");
    }

}
