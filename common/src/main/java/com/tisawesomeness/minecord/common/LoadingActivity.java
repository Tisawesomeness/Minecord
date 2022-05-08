package com.tisawesomeness.minecord.common;

import com.tisawesomeness.minecord.common.util.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * The activity message that displays when the bot is partially logged in and loading.
 */
@Value
public class LoadingActivity {

    /** Max number of characters allowed in a presence message, sanity check */
    public static final int MAX_CONTENT_LENGTH = 128;

    @JsonProperty("type") @JsonSetter(nulls = Nulls.SET)
    @Nullable PresenceType type;
    @JsonProperty("content") @JsonSetter(nulls = Nulls.SET)
    @Nullable String content;

    /**
     * Checks if a presence type is defined, then content is as well
     * @return The Verification
     */
    public Verification verify() {
        if (type != null && content == null) {
            return Verification.invalid("If the presence type is set, you must also set content.");
        }
        return Verification.valid();
    }

    /**
     * @return True if this presence is an activity with content, false if it's only an online status
     */
    public boolean hasContent() {
        return content != null;
    }

    /**
     * @return This loading activity as a JDA activity
     */
    public @Nullable Activity asActivity() {
        if (!hasContent()) {
            return null;
        }
        Activity.ActivityType activityType = Objects.requireNonNull(type).getActivityType();
        String trimmed = content.substring(0, Math.min(content.length(), MAX_CONTENT_LENGTH));
        return Activity.of(activityType, trimmed);
    }

}
