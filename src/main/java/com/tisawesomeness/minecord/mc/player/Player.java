package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.URLs;
import com.tisawesomeness.minecord.util.UUIDs;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.net.URL;
import java.time.Instant;
import java.util.*;

/**
 * Represents a single Minecraft player identified by a unique UUID.
 * <br>Note: this class has a natural ordering that is inconsistent with equals.
 */
@Value
public class Player implements Comparable<Player> {
    private static final Comparator<Player> COMPARATOR = initComparator();

    public static final URL STEVE_SKIN_URL = URLs.createUrl("https://textures.minecraft.net/texture/" +
            "1a4af718455d4aab528e7a61f86fa25e6a369d1768dcb13f7df319a713eb810b");
    public static final URL ALEX_SKIN_URL = URLs.createUrl("https://textures.minecraft.net/texture/" +
            "3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3");
    private static final Username DINNERBONE = new Username("Dinnerbone");
    private static final Username GRUMM = new Username("Grumm");
    private static final Username JEB = new Username("jeb_");

    /**
     * The unique ID of the player. When (extremely rarely) a player's UUID changes, the latest is used, and UUIDs
     * with invalid characters (this has happened before) are unsupported.
     */
    @NonNull UUID uuid;
    /**
     * A non-empty list of name changes, sorted from latest to earliest, including the original name
     */
    List<NameChange> nameHistory;
    /**
     * Contains additional profile information about the player, null if the player is PHD
     */
    @Nullable Profile profile;
    /**
     * The time this player was requested
     */
    @NonNull Instant requestTime;

    /**
     * Creates a new player representation.
     * @param uuid The unique ID of the player
     * @param nameHistory A list of name changes, <b>assumed to be sorted</b> according to the natural ordering
     *                    (see the {@link NameChange} docs)
     * @param profile Additional information about the player, null if PHD
     */
    public Player(@NonNull UUID uuid, List<NameChange> nameHistory, @Nullable Profile profile) {
        this.uuid = uuid;
        this.nameHistory = Collections.unmodifiableList(nameHistory);
        this.profile = profile;
        requestTime = Instant.now();
    }

    /**
     * Gets the player's profile data.
     * @return the profile
     * @throws IllegalStateException If the player is PHD, since PHD players have no profile
     */
    public @NonNull Profile getProfile() {
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a profile");
        }
        return profile;
    }

    /**
     * @return The player's current username
     */
    public @NonNull Username getUsername() {
        return nameHistory.get(0).getUsername();
    }

    /**
     * Checks if a player is pseudo hard-deleted, or <strong>PHD</strong>.
     * PHD players can be looked up in the Mojang API by UUID, but not by name. They are blocked by the game but not
     * completely deleted from Mojang's database.
     * PHD players do not have {@link Profile} data, and so do not have a known account type, skin, or cape.
     * @return whether the player is PHD
     */
    public boolean isPHD() {
        return profile == null;
    }

    /**
     * @return The skin type of the player's current skin
     * @throws IllegalStateException If the player is PHD
     */
    public SkinType getSkinType() {
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a profile");
        }
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
     * @throws IllegalStateException If the player is PHD
     */
    public boolean hasCustomSkin() {
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a profile");
        }
        Optional<URL> skinUrlOpt = profile.getSkinUrl();
        if (!skinUrlOpt.isPresent()) {
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
     * @throws IllegalStateException If the player is PHD
     */
    public @NonNull URL getSkinUrl() {
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a profile");
        }
        Optional<URL> skinUrl = profile.getSkinUrl();
        if (skinUrl.isPresent()) {
            return skinUrl.get();
        } else if (getDefaultSkinType() == SkinType.STEVE) {
            return STEVE_SKIN_URL;
        }
        return ALEX_SKIN_URL;
    }

    /**
     * Creates a render of this player.
     * @param type The type of render
     * @param overlay Whether to show the second skin layer, or overlay
     */
    public @NonNull Render createRender(RenderType type, boolean overlay) {
        return new Render(uuid, type, overlay);
    }
    /**
     * Creates a render of this player.
     * @param type The type of render
     * @param overlay Whether to show the second skin layer, or overlay
     * @param scale The scale of the render, capped at {@link RenderType#getMaxScale()}
     * @throws IllegalArgumentException If the scale is zero or negative
     */
    public @NonNull Render createRender(RenderType type, boolean overlay, int scale) {
        return new Render(uuid, type, overlay, scale);
    }

    /**
     * @return A link to the player's Optifine cape image, may not actually exist
     */
    public @NonNull URL getOptifineCapeUrl() {
        String encodedName = URLs.encode(getUsername().toString());
        return URLs.createUrl(String.format("http://s.optifine.net/capes/%s.png", encodedName));
    }

    /**
     * @return A link to the player's NameMC profile
     */
    public @NonNull URL getNameMCUrl() {
        return getNameMCUrlFor(getUsername());
    }
    /**
     * @param username The username of the player
     * @return A link to the player's NameMC profile
     */
    public static @NonNull URL getNameMCUrlFor(@NonNull Username username) {
        String encodedName = URLs.encode(username.toString());
        return URLs.createUrl("https://namemc.com/profile/" + encodedName);
    }
    /**
     * @param uuid The UUID of the player
     * @return A link to the player's NameMC profile
     */
    public static @NonNull URL getNameMCUrlFor(@NonNull UUID uuid) {
        return URLs.createUrl("https://namemc.com/profile/" + uuid);
    }

    /**
     * @return A link to the player's MCSkinHistory profile
     */
    public @NonNull URL getMCSkinHistoryUrl() {
        return URLs.createUrl("https://mcskinhistory.com/player/" + uuid);
    }

    /**
     * @return If the player is upside down as an easter egg
     */
    public boolean isUpsideDown() {
        return isUpsideDown(getUsername());
    }
    /**
     * @return If the player is upside down as an easter egg
     */
    public static boolean isUpsideDown(@NonNull Username username) {
        return username.equals(DINNERBONE) || username.equals(GRUMM);
    }
    /**
     * @return If the player makes sheep rainbow colored as an easter egg
     */
    public boolean isRainbow() {
        return isRainbow(getUsername());
    }
    /**
     * @return If the player makes sheep rainbow colored as an easter egg
     */
    public static boolean isRainbow(@NonNull Username username) {
        return username.equals(JEB);
    }

    /**
     * Compares this player to another alphabetically by username, then by UUID, then by request time
     * @param other The other player to compare to
     * @return -1, 0, or 1 if this player is less than, equal to,
     * or greater than the other player respectively
     */
    public int compareTo(@NonNull Player other) {
        return COMPARATOR.compare(this, other);
    }

    /**
     * Checks if this object is equal to another by comparing the UUID
     * @param o The other object to compare to
     * @return True only if this object is equal to the other object
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        Player otherPlayer = (Player) o;
        return uuid.equals(otherPlayer.uuid);
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
        return "P:" + UUIDs.toShortString(uuid) + "@" + requestTime.toEpochMilli();
    }

    private static Comparator<Player> initComparator() {
        return Comparator.comparing(Player::getUsername)
                .thenComparing(Player::getUuid)
                .thenComparing(Player::getRequestTime);
    }

}
