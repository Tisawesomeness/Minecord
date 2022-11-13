package com.tisawesomeness.minecord.mc.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * An enum with every possible skin model type.
 */
@RequiredArgsConstructor
public enum SkinModel {
    /**
     * The skin model with slim arms
     */
    SLIM("Alex (slim arms)", "slim"),
    /**
     * The skin model with square arms
     */
    WIDE("Steve (square arms)", "wide");

    @Getter private final @NonNull String description;
    private final @NonNull String label;

    @Override
    public String toString() {
        return label;
    }
}
