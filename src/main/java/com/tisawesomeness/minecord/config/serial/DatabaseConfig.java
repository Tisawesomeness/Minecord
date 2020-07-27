package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class DatabaseConfig {
    @JsonProperty("path")
    String path;
}
