package com.tisawesomeness.minecord.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;

/**
 * An enum of all activity types that can be used for the bot
 */
@RequiredArgsConstructor
public enum PresenceType {
    PLAYING(Activity.ActivityType.DEFAULT),
    STREAMING(Activity.ActivityType.STREAMING),
    LISTENING(Activity.ActivityType.LISTENING),
    COMPETING(Activity.ActivityType.COMPETING);

    @Getter private final Activity.ActivityType activityType;
}
