package com.tisawesomeness.minecord.mc.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

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

    /**
     * @return The default skin model according to the UUID
     */
    public static SkinModel defaultFor(UUID uuid) {
        return uuid.hashCode() % 2 == 0 ? WIDE : SLIM;
    }

    @Override
    public String toString() {
        return label;
    }
}
