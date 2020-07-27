package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.Lang;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains all default settings.
 */
@Value
public class SettingsConfig {
    @JsonProperty("defaultPrefix")
    String defaultPrefix;
    @JsonProperty("defaultLang")
    Lang defaultLang;
    @JsonProperty("defaultUseMenus")
    boolean defaultUseMenus;
}
