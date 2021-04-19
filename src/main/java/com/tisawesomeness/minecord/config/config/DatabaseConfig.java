package com.tisawesomeness.minecord.config.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import lombok.Value;

/**
 * Configures the database connection.
 */
@Value
public class DatabaseConfig {
    @JsonProperty("path") @ToString.Exclude
    String path;
}
