package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Configures the behavior of various caches.
 */
@Value
public class CacheConfig {
    private static final int MOJANG_RATELIMIT = 60;

    @JsonProperty("mojangUuidLifetime")
    int mojangUuidLifetime;
    @JsonProperty("mojangPlayerLifetime")
    int mojangPlayerLifetime;

    /**
     * Verifies that all cache values are valid
     * @return The Verification
     */
    public Verification verify() {
        return Verification.combineAll(
                verifyUuid(),
                verifyPlayer()
        );
    }

    private Verification verifyUuid() {
        if (mojangUuidLifetime >= MOJANG_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid("UUIDs must be in the cache for at least " + MOJANG_RATELIMIT + " seconds");
    }
    private Verification verifyPlayer() {
        if (mojangUuidLifetime >= MOJANG_RATELIMIT) {
            return Verification.valid();
        }
        return Verification.invalid("Players must be in the cache for at least " + MOJANG_RATELIMIT + " seconds");
    }

}
