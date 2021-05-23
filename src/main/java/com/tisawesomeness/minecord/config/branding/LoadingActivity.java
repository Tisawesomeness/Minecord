package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.BotBranding;
import com.tisawesomeness.minecord.config.config.SettingsConfig;
import com.tisawesomeness.minecord.util.discord.PresenceType;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;
import net.dv8tion.jda.api.entities.Activity;

import javax.annotation.Nullable;
import java.util.Objects;

@Value
public class LoadingActivity {
    @JsonProperty("type") @JsonSetter(nulls = Nulls.SET)
    @Nullable PresenceType type;
    @JsonProperty("content") @JsonSetter(nulls = Nulls.SET)
    @Nullable String content;

    public static LoadingActivity getDefault() {
        return new LoadingActivity(PresenceType.PLAYING, "Loading...");
    }

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
    public @Nullable Activity asActivity(BotBranding branding, SettingsConfig config) {
        if (!hasContent()) {
            return null;
        }
        Activity.ActivityType activityType = Objects.requireNonNull(type).getActivityType();
        return Activity.of(activityType, branding.parseConstants(content, config));
    }

}
