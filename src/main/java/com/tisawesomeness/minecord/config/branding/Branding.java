package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.config.VerifiableConfig;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Contains all the values that change how the bot presents itself, mirroring {@code branding.yml}
 * @see com.tisawesomeness.minecord.config.config.Config Config
 */
@Value
public class Branding implements VerifiableConfig {
    @JsonProperty("branding")
    BrandingConfig brandingConfig;
    @JsonProperty("announcements")
    AnnouncementConfig announcementConfig;
    @JsonProperty("presence")
    PresenceConfig presenceConfig;

    public Verification verify() {
        return Verification.combineAll(
                announcementConfig.verify(),
                presenceConfig.verify()
        );
    }

}
