package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.common.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;

/**
 * Overrides a command cooldown
 */
@Value
public class CommandOverride {
    @JsonProperty("cooldown") @JsonSetter(nulls = Nulls.SET)
    @Nullable Integer cooldown; // Using wrapper class since default 0 would override the default cooldown
    @JsonProperty("cooldownPool") @JsonSetter(nulls = Nulls.SET)
    @Nullable String cooldownPool;
    @JsonProperty("disabled") @JsonSetter(nulls = Nulls.SET)
    boolean disabled;

    public Verification verify() {
        return Verification.verify(cooldown == null || cooldownPool == null,
                "You cannot include both a cooldown and cooldown pool in the same command.");
    }
}
