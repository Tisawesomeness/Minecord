package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains many boolean fields (flags) that turn bot features on/off.
 */
@Value
public class FlagConfig {
    @JsonProperty("debugMode")
    boolean debugMode;
    @JsonProperty("useAnnouncements")
    boolean useAnnouncements;
    @JsonProperty("respondToMentions")
    boolean respondToMentions;
    @JsonProperty("sendTyping")
    boolean sendTyping;
    @JsonProperty("showExtraInfo")
    boolean showExtraInfo;
    @JsonProperty("elevatedSkipCooldown")
    boolean elevatedSkipCooldown;
    @JsonProperty("useElectroidAPI")
    boolean useElectroidAPI;
}
