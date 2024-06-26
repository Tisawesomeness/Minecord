package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Provides additional account information about a player.
 */
@Value
@AllArgsConstructor
public class Profile {

    private static final String MOJANG_STUDIOS_CAPE_URL = "https://minecraft.wiki/images/Mojang_Studios_Cape_%28Texture%29.png?7450c";
    private static final URL FIXED_MOJANG_CAPE_URL = UrlUtils.createUrl("https://static.wikia.nocookie.net/minecraft_gamepedia/images/5/59/Mojang_Cape_(texture).png");

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
     * The skin model of the skin URL (only valid if the skin url exists)
     */
    SkinModel skinModel;
    @Nullable URL skinUrl;
    @Nullable URL capeUrl;
    Set<ProfileAction> profileActions;

    public Profile(@NonNull Username username, boolean legacy, boolean demo, SkinModel skinModel, @Nullable URL skinUrl,
                   @Nullable URL capeUrl) {
        this(username, legacy, demo, skinModel, skinUrl, capeUrl, Collections.emptySet());
    }

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
