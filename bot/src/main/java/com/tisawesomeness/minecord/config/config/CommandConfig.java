package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.share.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;
import java.util.Objects;

/**
 * Configures command cooldowns
 */
@Value
public class CommandConfig {
    @JsonProperty("defaultCooldown")
    int defaultCooldown;
    @JsonProperty("pushUsesInterval")
    int pushUsesInterval;
    @JsonProperty("overrides")
    Map<String, CommandOverride> overrides;
    @JsonProperty("cooldownPools")
    Map<String, Integer> cooldownPools;

    public Verification verify() {
        return Verification.combineAll(
                verifyPushUsesInterval(),
                verifyOverrides(),
                verifyPoolsExist()
        );
    }
    private Verification verifyPushUsesInterval() {
        return Verification.verify(pushUsesInterval > 0, "pushUsesInterval must be positive.");
    }
    private Verification verifyOverrides() {
        return overrides.values().stream()
                .map(CommandOverride::verify)
                .reduce(Verification::combine)
                .orElse(Verification.valid());
    }
    private Verification verifyPoolsExist() {
        boolean poolsExist = overrides.values().stream()
                .map(CommandOverride::getCooldownPool)
                .filter(Objects::nonNull)
                .allMatch(cooldownPools::containsKey);
        return Verification.verify(poolsExist, "One of the cooldown pools does not exist.");
    }
}
