package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.common.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AdvDiscordConfig {
    @JsonProperty("cooldownTolerance")
    double cooldownTolerance;

    public Verification verify() {
        if (cooldownTolerance >= 0.0) {
            return Verification.valid();
        }
        return Verification.invalid("Cooldown tolerance cannot be negative");
    }
}
