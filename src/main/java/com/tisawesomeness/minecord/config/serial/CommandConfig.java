package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;

/**
 * Configures command cooldowns
 */
@Value
public class CommandConfig {
    @JsonProperty("defaultCooldown")
    int defaultCooldown;
    @JsonProperty("overrides")
    Map<String, CommandOverride> overrides;
}
