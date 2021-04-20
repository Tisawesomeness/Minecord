package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.util.Discord;
import com.tisawesomeness.minecord.util.discord.PresenceType;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents a presence that cane be only a status,
 * a playing/listening activity with content,
 * or a streaming activity with URL.
 */
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Presence {
    @JsonProperty("status")
    OnlineStatus status;
    @JsonProperty("type") @JsonSetter(nulls = Nulls.SET)
    @Nullable PresenceType type;
    @JsonProperty("content") @JsonSetter(nulls = Nulls.SET)
    @Nullable String content;
    @JsonProperty("url") @JsonSetter(nulls = Nulls.SET)
    @Nullable String url;

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
     * Changes the bot presence to this one
     * @param sm The ShardManager used to change presence
     */
    public void setPresence(ShardManager sm) {
        if (!hasContent()) {
            sm.setPresence(status, null);
            return;
        }
        String displayContent = Discord.parseVariables(content, sm);
        Activity.ActivityType activityType = Objects.requireNonNull(type).getActivityType();
        Activity jdaActivity = Activity.of(activityType, displayContent, url);
        sm.setPresence(status, jdaActivity);
    }
}
