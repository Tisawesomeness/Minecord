package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.util.UrlUtils;
import lombok.NonNull;
import lombok.Value;

import java.net.URL;
import java.util.UUID;

/**
 * Represents a Crafatar player render.
 */
@Value
public class Render {
    @NonNull UUID player;
    RenderType type;
    boolean overlay;
    int scale;
    int providedScale;

    /**
     * Creates a render.
     * @param player The UUID of the player to render
     * @param type The type of render
     * @param overlay Whether to show the second skin layer, or overlay
     */
    public Render(@NonNull UUID player, RenderType type, boolean overlay) {
        this(player, type, overlay, type.getDefaultScale());
    }
    /**
     * Creates a render.
     * @param player The UUID of the player to render
     * @param type The type of render
     * @param overlay Whether to show the second skin layer, or overlay
     * @param scale The scale of the render, capped at {@link RenderType#getMaxScale()}
     * @throws IllegalArgumentException If the scale is zero or negative
     */
    public Render(@NonNull UUID player, RenderType type, boolean overlay, int scale) {
        if (scale < 1) {
            throw new IllegalArgumentException("The render scale must be positive but was " + scale);
        }
        this.player = player;
        this.type = type;
        this.overlay = overlay;
        this.scale = Math.min(scale, type.getMaxScale());
        providedScale = scale;
    }

    /**
     * Generates a URL linking to the render image.
     * @return The render's URL
     */
    public @NonNull URL render() {
        String query = type.isRender() ? "scale" : "size";
        String overlayStr = overlay ? "&overlay" : "";
        return UrlUtils.createUrl(String.format("%s%s/%s?%s=%d%s",
                Config.getCrafatarHost(), type.getBasePath(), player, query, scale, overlayStr));
    }
}
