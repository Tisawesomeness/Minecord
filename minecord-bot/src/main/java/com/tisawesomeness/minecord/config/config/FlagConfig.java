package com.tisawesomeness.minecord.config.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains many boolean fields (flags) that turn bot features on/off.
 */
@Value
public class FlagConfig {
    @JsonProperty("debugMode")
    boolean debugMode;
    @JsonProperty("loadTranslationsFromFile")
    boolean loadTranslationsFromFile;
    @JsonProperty("linkedDeletion")
    boolean linkedDeletion;
    @JsonProperty("respondToMentions")
    boolean respondToMentions;
    @JsonProperty("sendTyping")
    boolean sendTyping;
    @JsonProperty("showExtraInfo")
    boolean showExtraInfo;
    @JsonProperty("elevatedSkipCooldown")
    boolean elevatedSkipCooldown;
    @JsonProperty("elevatedBypassDisabled")
    boolean elevatedBypassDisabled;
    @JsonProperty("useElectroidAPI")
    boolean useElectroidAPI;
    @JsonProperty("useGappleAPI")
    boolean useGappleAPI;
}
