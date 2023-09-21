package com.tisawesomeness.minecord.mc.player;

import lombok.Value;

import java.net.URL;
import java.util.Optional;
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
    /**
     * Gets the default skin hosted by the given URL, for versions 1.19.3+
     * @param url the skin URL
     * @return the default skin, or empty if the URL is for a custom skin
     */
    public static Optional<DefaultSkin> fromUrl(URL url) {
        for (DefaultSkinType type : DefaultSkinType.values()) {
            if (type.getUrl(SkinModel.WIDE).sameFile(url)) {
                return Optional.of(new DefaultSkin(SkinModel.WIDE, type));
            }
            if (type.getUrl(SkinModel.SLIM).sameFile(url)) {
                return Optional.of(new DefaultSkin(SkinModel.SLIM, type));
            }
        }
        return Optional.empty();
    }

    /**
     * @return the URL of the default skin
     */
    public URL getURL() {
        return type.getUrl(model);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", type, model);
    }
}
