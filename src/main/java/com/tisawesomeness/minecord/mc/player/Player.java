package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.UUIDUtils;
import com.tisawesomeness.minecord.util.network.URLUtils;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a single Minecraft player identified by a unique UUID.
 */
@Value
public class Player implements Comparable<Player> {
    public static final URL STEVE_SKIN_URL = URLUtils.createUrl("https://textures.minecraft.net/texture/" +
            "1a4af718455d4aab528e7a61f86fa25e6a369d1768dcb13f7df319a713eb810b");
    public static final URL ALEX_SKIN_URL = URLUtils.createUrl("https://textures.minecraft.net/texture/" +
            "3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3");

    /**
     * The unique ID of the player. When (extremely rarely) a player's UUID changes, the latest is used, and UUIDs
     * with invalid characters (this has happened before) are unsupported.
     */
    @NonNull UUID uuid;
    /**
     * The player's current username
     */
    @NonNull Username username;
    /**
     * A non-empty list of name changes, sorted from latest to earliest, including the original name
     */
    List<NameChange> nameHistory;
    /**
     * Contains additional profile information about the player
     */
    @NonNull Profile profile;

    /**
     * @return The skin type of the player's current skin
     */
    public SkinType getSkinType() {
        if (profile.getSkinUrl().isPresent()) {
            return profile.getSkinType();
        }
        return getDefaultSkinType();
    }
    /**
     * @return The default skin type according to the UUID
     */
    public SkinType getDefaultSkinType() {
        return getDefaultSkinTypeFor(uuid);
    }
    /**
     * @return The default skin type according to the UUID
     */
    public static SkinType getDefaultSkinTypeFor(UUID uuid) {
        return uuid.hashCode() % 2 == 0 ? SkinType.STEVE : SkinType.ALEX;
    }

    /**
     * @return True if the player has a custom skin
     */
    public boolean hasCustomSkin() {
        Optional<URL> skinUrlOpt = profile.getSkinUrl();
        if (skinUrlOpt.isEmpty()) {
            return false;
        }
        URL skinUrl = skinUrlOpt.get();
        if (getDefaultSkinType() == SkinType.STEVE) {
            return !skinUrl.sameFile(STEVE_SKIN_URL);
        }
        return !skinUrl.sameFile(ALEX_SKIN_URL);
    }

    /**
     * Gets the URL to the player's custom skin texture, or the default steve or alex texture if no custom skin is set.
     * @return The URL where Mojang hosts the skin texture
     */
    public @NonNull URL getSkinUrl() {
        Optional<URL> skinUrl = profile.getSkinUrl();
        if (skinUrl.isPresent()) {
            return skinUrl.get();
        } else if (getDefaultSkinType() == SkinType.STEVE) {
            return STEVE_SKIN_URL;
        }
        return ALEX_SKIN_URL;
    }

    /**
     * @return A link to the player's Crafatr avatar image
     */
    public @NonNull URL getAvatarUrl() {
        return getAvatarUrlFor(uuid);
    }
    /**
     * @return A link to the player's Crafatr avatar image
     */
    public static @NonNull URL getAvatarUrlFor(UUID uuid) {
        return URLUtils.createUrl("https://crafatar.com/avatars/" + uuid);
    }
    /**
     * @return A link to the player's Crafatar body render
     */
    public @NonNull URL getBodyUrl() {
        return URLUtils.createUrl("https://crafatar.com/renders/body/" + uuid);
    }

    /**
     * @return A link to the player's NameMC profile
     */
    public @NonNull URL getNameMCUrl() {
        return getNameMCUrlFor(username);
    }
    /**
     * @param username The username of the player
     * @return A link to the player's NameMC profile
     */
    public static @NonNull URL getNameMCUrlFor(@NonNull Username username) {
        String encodedName = URLEncoder.encode(username.toString(), StandardCharsets.UTF_8);
        return URLUtils.createUrl("https://namemc.com/profile/" + encodedName);
    }
    /**
     * @param uuid The UUID of the player
     * @return A link to the player's NameMC profile
     */
    public static @NonNull URL getNameMCUrlFor(@NonNull UUID uuid) {
        return URLUtils.createUrl("https://namemc.com/profile/" + uuid);
    }

    /**
     * @return A link to the player's MCSkinHistory profile
     */
    public @NonNull URL getMCSkinHistoryUrl() {
        return URLUtils.createUrl("https://mcskinhistory.com/player/" + uuid);
    }

    /**
     * Compares this player to another alphabetically by username
     * @param other The other player to compare to
     * @return -1, 0, or 1 if this player is less than, equal to,
     * or greater than the other player respectively
     */
    public int compareTo(@NonNull Player other) {
        return username.compareTo(other.username);
    }

    /**
     * Checks if this object is equal to another by comparing the UUID
     * @param other The other object to compare to
     * @return True only if this object is equal to the other object
     */
    @Override
    public boolean equals(@Nullable Object other) {
        if (other instanceof Player) {
            Player otherPlayer = (Player) other;
            return uuid.equals(otherPlayer.uuid);
        }
        return false;
    }
    /**
     * @return The hash code of the player's UUID
     * @see UUID#hashCode()
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public @NonNull String toString() {
        return "P:" + UUIDUtils.toShortString(uuid);
    }

}
