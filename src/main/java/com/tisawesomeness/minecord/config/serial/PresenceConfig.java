package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.config.PresenceBehavior;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import java.util.List;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceConfig {
    @JsonProperty("changeInterval") @JsonSetter(nulls = Nulls.SET)
    int changeInterval;
    @JsonProperty("behavior") @JsonSetter(nulls = Nulls.SET)
    PresenceBehavior behavior;
    @JsonProperty("presences")
    List<PresenceConfigEntry> presences;

    public Verification verify() {
        if (presences.isEmpty()) {
            return Verification.valid();
        }
        return Verification.combineAll(
                verifyChangeInterval(),
                verifyBehavior(),
                verifyPresences()
        );
    }
    // Assumes presences is not empty
    private Verification verifyChangeInterval() {
        if (changeInterval < -1 || changeInterval == 0) {
            return Verification.invalid("The change interval must be -1 or positive.");
        }
        return Verification.valid();
    }
    // Assumes presences is empty
    private Verification verifyBehavior() {
        if (behavior == null) {
            return Verification.invalid("You must provide the presence behavior.");
        }
        return Verification.valid();
    }
    private Verification verifyPresences() {
        return presences.stream()
                .map(PresenceConfigEntry::verify)
                .reduce(Verification::combine)
                .orElse(Verification.valid());
    }

}
