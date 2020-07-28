package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Overrides a command cooldown
 */
@Value
public class CommandOverride {
    @JsonProperty("cooldown")
    int cooldown;
}
