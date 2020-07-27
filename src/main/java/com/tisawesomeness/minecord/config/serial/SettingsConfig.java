package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.Lang;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class SettingsConfig {
    @JsonProperty("prefixDefault")
    String prefixDefault;
    @JsonProperty("langDefault")
    Lang langDefault;
    @JsonProperty("useMenusDefault")
    boolean useMenusDefault;
}
