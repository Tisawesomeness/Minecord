package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.lang.Localizable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An enum with every possible skin model type.
 */
@RequiredArgsConstructor
public enum SkinType implements Localizable {
    /**
     * The skin model with square arms
     */
    STEVE("steve"),
    /**
     * The skin model with slim arms
     */
    ALEX("alex");

    private final @NonNull String key;
    public @NonNull String getTranslationKey() {
        return "mc.player.skin." + key;
    }
    public Object[] getTranslationArgs() {
        return new Object[0];
    }

}
