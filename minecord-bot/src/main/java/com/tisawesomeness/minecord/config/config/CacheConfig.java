package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.common.util.Verification;

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

    /**
     * Verifies that all cache values are valid
     * @return The Verification
     */
    public Verification verify() {
        return Verification.combineAll(
                AdvancedConfig.verifyCacheLifetime(guildLifetime, "Guild"),
                AdvancedConfig.verifyCacheMaxSize(guildMaxSize, "Guild"),
                AdvancedConfig.verifyCacheLifetime(channelLifetime, "Channel"),
                AdvancedConfig.verifyCacheMaxSize(channelMaxSize, "Channel"),
                AdvancedConfig.verifyCacheLifetime(userLifetime, "User"),
                AdvancedConfig.verifyCacheMaxSize(userMaxSize, "User")
        );
    }

}
