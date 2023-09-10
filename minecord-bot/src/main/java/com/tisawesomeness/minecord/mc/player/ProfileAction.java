package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;

import java.util.Optional;

/** A moderation action taken against a profile */
public enum ProfileAction {
    FORCED_NAME_CHANGE,
    USING_BANNED_SKIN;

    public static Optional<ProfileAction> from(@NonNull String str) {
        for (ProfileAction action : values()) {
            if (action.toString().equals(str)) {
                return Optional.of(action);
            }
        }
        return Optional.empty();
    }
}
