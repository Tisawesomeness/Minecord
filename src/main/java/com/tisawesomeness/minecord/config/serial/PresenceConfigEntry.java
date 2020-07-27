package com.tisawesomeness.minecord.config.serial;

import com.tisawesomeness.minecord.util.DiscordUtils;
import com.tisawesomeness.minecord.util.type.Verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PresenceConfigEntry {
    @JsonProperty("status")
    OnlineStatus status;
    @JsonProperty("type") @JsonSetter(nulls = Nulls.SET)
    PresenceType type;
    @JsonProperty("content") @JsonSetter(nulls = Nulls.SET)
    String content;
    @JsonProperty("url") @JsonSetter(nulls = Nulls.SET)
    String url;

    public Verification verify() {
        if (type != null && content == null) {
            return Verification.invalid("If the presence type is set, you must also set content.");
        }
        return Verification.valid();
    }

    public boolean hasContent() {
        return content != null;
    }

    public void setPresence(ShardManager sm) {
        if (type == null) {
            sm.setPresence(status, null);
            return;
        }
        Activity jdaActivity = Activity.of(type.getActivityType(), DiscordUtils.parseVariables(content, sm), url);
        sm.setPresence(status, jdaActivity);
    }
}
