package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Configures the database connection.
 */
@Value
public class DatabaseConfig {
    @JsonProperty("path")
    String path;
}
