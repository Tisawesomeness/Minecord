package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class FlagsConfig {
    @JsonProperty("debugMode")
    boolean debugMode;
    @JsonProperty("useAnnouncements")
    boolean useAnnouncements;
    @JsonProperty("respondToMentions")
    boolean respondToMentions;
    @JsonProperty("sendTyping")
    boolean sendTyping;
    @JsonProperty("showMemory")
    boolean showMemory;
    @JsonProperty("elevatedSkipCooldown")
    boolean elevatedSkipCooldown;
}
