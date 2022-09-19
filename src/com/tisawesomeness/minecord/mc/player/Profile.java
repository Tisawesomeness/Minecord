package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.UrlUtils;

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

    private static final String MOJANG_STUDIOS_CAPE_URL = "https://textures.minecraft.net/texture/" +
            "9e507afc56359978a3eb3e32367042b853cddd0995d17d0da995662913fb00f7";
    private static final URL FIXED_MOJANG_CAPE_URL = UrlUtils.createUrl("https://static.wikia.nocookie.net/" +
            "minecraft_gamepedia/images/5/59/Mojang_Cape_(texture).png");

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
        if (capeUrl != null && capeUrl.toString().equals(MOJANG_STUDIOS_CAPE_URL)) {
            return Optional.of(FIXED_MOJANG_CAPE_URL);
        }
        return Optional.ofNullable(capeUrl);
    }

}
