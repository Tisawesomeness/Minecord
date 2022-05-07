package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.share.config.VerifiableConfig;
import com.tisawesomeness.minecord.share.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

import javax.annotation.Nullable;

/**
 * Contains all the values that change how the bot presents itself, mirroring {@code branding.yml}
 * <br>This class assumes it is being parsed with the
 * {@link com.tisawesomeness.minecord.share.config.ConfigReader} settings.
 * @see com.tisawesomeness.minecord.config.config.Config Config
 */
@Value
public class Branding implements VerifiableConfig {
    @JsonProperty("branding") @JsonSetter(nulls = Nulls.SET)
    @Nullable BrandingConfig brandingConfig;
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
