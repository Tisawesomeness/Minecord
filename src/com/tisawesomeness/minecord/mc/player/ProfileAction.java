package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;
import org.json.JSONArray;

import java.util.*;

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

    public static Set<ProfileAction> parseProfileActions(JSONArray arr) {
        if (arr == null) {
            return Collections.emptySet();
        }
        Set<ProfileAction> profileActions = EnumSet.noneOf(ProfileAction.class);
        for (int i = 0; i < arr.length(); i++) {
            String actionStr = arr.getString(i);
            ProfileAction.from(actionStr.toUpperCase(Locale.ROOT)).ifPresent(profileActions::add);
        }
        return profileActions;
    }

}
