package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An enum with every possible skin model type.
 */
@RequiredArgsConstructor
public enum SkinType {
    /**
     * The skin model with square arms
     */
    STEVE("steve"),
    /**
     * The skin model with slim arms
     */
    ALEX("alex");

    /**
     * A description of the skin model type
     */
    private final @NonNull String key;
    public @NonNull String getTranslationKey() {
        return "mc.player.skin." + key;
    }

}
