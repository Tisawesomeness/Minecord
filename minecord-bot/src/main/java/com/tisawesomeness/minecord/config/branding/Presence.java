package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.common.PresenceType;
import com.tisawesomeness.minecord.common.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nullable;

/**
 * Represents a presence that cane be only a status,
 * a playing/listening activity with content,
 * or a streaming activity with URL.
 */
@Value
@Slf4j
public class Presence {
    @JsonProperty("status")
    OnlineStatus status;
    @JsonProperty("type") @JsonSetter(nulls = Nulls.SET)
    @Nullable PresenceType type;
    @JsonProperty("content") @JsonSetter(nulls = Nulls.SET)
    @Nullable String content;
    @JsonProperty("url") @JsonSetter(nulls = Nulls.SET)
    @Nullable String url;
    @JsonProperty("weight") @JsonSetter(nulls = Nulls.SET)
    @Nullable Integer weight;

    public int getWeight() {
        return weight == null ? 1 : weight;
    }

    /**
     * Checks if a presence type is defined, then content is as well
     * @return The Verification
     */
    public Verification verify() {
        if (type == PresenceType.STREAMING && url != null && !Activity.isValidStreamingUrl(url)) {
            log.warn("Presence url " + url + " is not a valid streaming (Twitch or YouTube) URL");
        }
        return Verification.combineAll(
                verifyContent(),
                verifyWeight()
        );
    }
    private Verification verifyContent() {
        if (type != null) {
            if (content == null) {
                return Verification.invalid("If the presence type is set, you must also set content.");
            }
            if (content.isEmpty()) {
                return Verification.invalid("Presence content cannot be empty.");
            }
        }
        return Verification.valid();
    }
    private Verification verifyWeight() {
        return Verification.verify(weight == null || weight >= 0, "Presence weight must be non-negative");
    }

    /**
     * @return True if this presence is an activity with content, false if it's only an online status
     */
    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

}
