package com.tisawesomeness.minecord.config.config;

import com.tisawesomeness.minecord.common.util.Verification;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.setting.impl.PrefixSetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains all default settings.
 */
@Value
public class SettingsConfig {
    @JsonProperty("defaultPrefix")
    String defaultPrefix;
    @JsonProperty("maxPrefixLength")
    int maxPrefixLength;
    @JsonProperty("defaultLang")
    Lang defaultLang;
    @JsonProperty("defaultUseMenus")
    boolean defaultUseMenus;

    public Verification verify() {
        return Verification.combineAll(
                verifyMaxLength(),
                new PrefixSetting(this).resolve(defaultPrefix).asVerification()
        );
    }
    private Verification verifyMaxLength() {
        if (maxPrefixLength < 1 || PrefixSetting.MAX_LENGTH < maxPrefixLength) {
            return Verification.invalid("Max prefix length must be between 1 and " + PrefixSetting.MAX_LENGTH);
        }
        return Verification.valid();
    }

}
