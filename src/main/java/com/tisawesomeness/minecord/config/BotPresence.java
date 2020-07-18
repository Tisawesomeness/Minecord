package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.util.DiscordUtils;

import lombok.NonNull;
import lombok.ToString;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONObject;

import javax.annotation.Nullable;

/**
 * An extension of {@link Activity} that can have bot variables and constants.
 */
@ToString
public class BotPresence {
    private final @NonNull Activity.ActivityType type;
    private final @NonNull String content;
    private final @Nullable String url;
    private final @NonNull OnlineStatus status;

    /**
     * Creates a new presence from the JSON input with parsed constants.
     * @param obj A JSONObject with {@code type} and {@code content} fields.
     * @param config The config file used to get constants.
     */
    public BotPresence(@NonNull JSONObject obj, @NonNull Config config) {
        String typeStr = obj.getString("type");
        if ("playing".equalsIgnoreCase(typeStr)) {
            type = Activity.ActivityType.DEFAULT;
            url = null;
        } else if ("streaming".equalsIgnoreCase(typeStr)) {
            type = Activity.ActivityType.STREAMING;
            url = obj.optString("url");
        } else if ("listening".equalsIgnoreCase(typeStr)) {
            type = Activity.ActivityType.LISTENING;
            url = null;
        } else {
            throw new IllegalArgumentException("Invalid activity type: " + typeStr);
        }
        content = DiscordUtils.parseConstants(obj.getString("content"), config);

        OnlineStatus parsedStatus = OnlineStatus.fromKey(obj.optString("status"));
        status = parsedStatus == OnlineStatus.UNKNOWN ? OnlineStatus.ONLINE : parsedStatus;
    }

    /**
     * Changes the bot's status to this presence.
     * @param sm The ShardManager to pull variables from
     */
    public void setPresence(@NonNull ShardManager sm) {
        Activity jdaActivity = Activity.of(type, DiscordUtils.parseVariables(content, sm), url);
        sm.setPresence(status, jdaActivity);
    }
}
