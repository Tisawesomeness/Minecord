package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.UrlUtils;
import lombok.NonNull;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

/**
 * Represents a Minotar player render.
 */
@Value
public class Render {
    @NonNull UUID player;
    RenderType type;
    boolean overlay;
    int width;

    /**
     * Creates a render.
     * @param player The UUID of the player to render
     * @param type The type of render
     * @param overlay Whether to show the second skin layer, or overlay
     * @param width The width (and height) of the render in pixels
     * @throws IllegalArgumentException If the width is zero or negative,
     * or if overlay is true and the render type does not support overlay
     */
    public Render(@NonNull UUID player, RenderType type, boolean overlay, int width) {
        if (width < 1) {
            throw new IllegalArgumentException("The render scale must be positive but was " + width);
        }
        if (overlay && !type.supportsOverlay()) {
            throw new IllegalArgumentException("Render type " + type + " does not support overlay");
        }
        this.player = player;
        this.type = type;
        this.overlay = overlay;
        this.width = width;
    }

    /**
     * Generates a URL linking to the render image.
     * @return The render's URL
     */
    public @NonNull URL render() {
        String path = overlay ? type.getOverlayPath() : type.getBasePath();
        return UrlUtils.createUrl(String.format("%s%s/%s/%s.png",
                Config.getMinotarHost(), path, player, width));
    }
}
