package com.tisawesomeness.minecord.config.serial;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Configures the branding of the bot, such as the website and invite links.
 */
@Value
public class BrandingConfig {
    @JsonProperty("author")
    String author;
    @JsonProperty("authorTag")
    String authorTag;
    @JsonProperty("invite")
    String invite;
    @JsonProperty("helpServer")
    String helpServer;
    @JsonProperty("website")
    String website;
    @JsonProperty("github")
    String github;
}
