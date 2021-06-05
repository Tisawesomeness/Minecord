package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.util.discord.PresenceBehavior;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Holds all the presences and determines how and how often to switch between them.
 */
@Value
public class PresenceConfig {
    @JsonProperty("changeInterval") @JsonSetter(nulls = Nulls.SET)
    int changeInterval;
    @JsonProperty("behavior") @JsonSetter(nulls = Nulls.SET)
    @Nullable PresenceBehavior behavior;
    @JsonProperty("presences")
    List<Presence> presences;
    @JsonProperty("loadingActivity") @JsonSetter(nulls = Nulls.SET)
    @Nullable LoadingActivity loadingActivity;

    /**
     * Checks if there are either no presences (no verification needed)
     * or the change interval and behavior meet requirements.
     * @return The Verification
     */
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
        return Verification.verify(changeInterval == -1 || changeInterval > 0,
                "The change interval must be -1 or positive.");
    }
    // Assumes presences is not empty
    private Verification verifyBehavior() {
        return Verification.verify(behavior != null, "You must provide the presence behavior.");
    }
    private Verification verifyPresences() {
        return presences.stream()
                .map(Presence::verify)
                .reduce(Verification::combine)
                .orElse(Verification.valid());
    }

}
