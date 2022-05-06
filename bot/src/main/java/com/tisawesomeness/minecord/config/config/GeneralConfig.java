package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.command.player.ProfileCommand;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains some general config options.
 */
@Value
public class GeneralConfig {
    @JsonProperty("helpMaxPrefixLength")
    int helpMaxPrefixLength;
    @JsonProperty("maxProfileNameChanges")
    int maxProfileNameChanges;

    public Verification verify() {
        return Verification.combineAll(
                verifyMaxHelpPrefixLength(),
                verifyMaxNameChanges()
        );
    }
    private Verification verifyMaxHelpPrefixLength() {
        return Verification.verify(helpMaxPrefixLength >= 0, "Max help prefix length cannot be negative");
    }
    private Verification verifyMaxNameChanges() {
        boolean isValid = maxProfileNameChanges == -1 ||
                (2 <= maxProfileNameChanges && maxProfileNameChanges <= ProfileCommand.MAX_NAME_CHANGES_NO_SPLIT);
        return Verification.verify(isValid, "Max profile name changes must be -1 or between 2 and " +
                ProfileCommand.MAX_NAME_CHANGES_NO_SPLIT);
    }

}
