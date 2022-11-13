package com.tisawesomeness.minecord.mc.player;

import lombok.Value;

import java.util.UUID;

/**
 * A default skin for 1.19.3+.
 */
@Value
public class DefaultSkin {
    SkinModel model;
    DefaultSkinType type;

    /**
     * Gets the default skin type for a given UUID, for versions 1.19.3+
     * @param uuid the UUID of the player
     * @return the default skin
     */
    public static DefaultSkin defaultFor(UUID uuid) {
        int n = Math.floorMod(uuid.hashCode(), 18);
        return new DefaultSkin(SkinModel.values()[n / 9], DefaultSkinType.values()[n % 9]);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", type, model);
    }
}
