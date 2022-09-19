package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Optional;

/**
 * Provides additional account information about a player.
 */
@Value
public class Profile {
    /**
     * The player's current username
     */
    @NonNull Username username;
    /**
     * Whether the player has not migrated to a Minecraft account (using email to log in)
     */
    boolean legacy;
    /**
     * Whether the player is a demo account
     */
    boolean demo;
    /**
     * The skin type of the skin URL (only valid if the skin url exists)
     */
    SkinType skinType;
    @Nullable URL skinUrl;
    @Nullable URL capeUrl;

    /**
     * @return The skin URL, or empty if the player has no <b>custom</b> skin
     */
    public Optional<URL> getSkinUrl() {
        return Optional.ofNullable(skinUrl);
    }
    /**
     * @return The skin URL, or empty if the player has no custom cape
     */
    public Optional<URL> getCapeUrl() {
        return Optional.ofNullable(capeUrl);
    }
}
