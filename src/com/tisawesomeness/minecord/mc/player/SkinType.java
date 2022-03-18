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
    STEVE("Steve (square arms)"),
    /**
     * The skin model with slim arms
     */
    ALEX("Alex (slim arms)");

    private final @NonNull String label;

    @Override
    public String toString() {
        return label;
    }
}
