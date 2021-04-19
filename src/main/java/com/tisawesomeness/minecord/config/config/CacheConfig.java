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

    @JsonProperty("linkLifetime")
    int linkLifetime;
    @JsonProperty("linkMaxSize")
    int linkMaxSize;

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
                verifyGuildLifetime(),
                verifyGuildMaxSize(),
                verifyChannelLifetime(),
                verifyChannelMaxSize(),
                verifyUserLifetime(),
                verifyUserMaxSize(),
                verifyLinkLifetime(),
                verifyLinkMaxSize(),
                verifyUuid(),
                verifyPlayer()
        );
    }

    private Verification verifyGuildLifetime() {
        if (guildLifetime >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("Guild lifetime cannot be negative");
    }
    private Verification verifyGuildMaxSize() {
        if (guildMaxSize >= -1) {
            return Verification.valid();
        }
        return Verification.invalid("Guild cache max size must be -1, 0, or positive");
    }
    private Verification verifyChannelLifetime() {
        if (channelLifetime >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("Channel lifetime cannot be negative");
    }
    private Verification verifyChannelMaxSize() {
        if (channelMaxSize >= -1) {
            return Verification.valid();
        }
        return Verification.invalid("Channel cache max size must be -1, 0, or positive");
    }
    private Verification verifyUserLifetime() {
        if (userLifetime >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("User lifetime cannot be negative");
    }
    private Verification verifyUserMaxSize() {
        if (userMaxSize >= -1) {
            return Verification.valid();
        }
        return Verification.invalid("User cache max size must be -1, 0, or positive");
    }

    private Verification verifyLinkLifetime() {
        if (linkLifetime >= 0) {
            return Verification.valid();
        }
        return Verification.invalid("Link lifetime cannot be negative");
    }
    private Verification verifyLinkMaxSize() {
        if (linkMaxSize >= -1) {
            return Verification.valid();
        }
        return Verification.invalid("Link cache max size must be -1, 0, or positive");
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

}
