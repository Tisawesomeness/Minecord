package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;
import com.tisawesomeness.minecord.util.type.Verification;

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

    public Verification verify() {
        return PrefixSetting.verify(defaultPrefix);
    }
}
