package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.util.type.Verification;

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
        if (pushUsesInterval > 0) {
            return Verification.valid();
        }
        return Verification.invalid("pushUsesInterval must be positive.");
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
        if (poolsExist) {
            return Verification.valid();
        }
        return Verification.invalid("One of the cooldown pools does not exist.");
    }
}
