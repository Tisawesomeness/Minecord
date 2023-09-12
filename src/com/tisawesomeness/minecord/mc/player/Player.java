package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.util.UrlUtils;
import com.tisawesomeness.minecord.util.UuidUtils;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.net.URL;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a single Minecraft player identified by a unique UUID.
 * <br>Note: this class has a natural ordering that is inconsistent with equals.
 */
@Value
public class Player implements Comparable<Player> {
    private static final Comparator<Player> COMPARATOR = initComparator();

    // 37 is max length of a UUID with dashes
    public static final int MAX_PLAYER_ARGUMENT_LENGTH = Math.max(37, Username.MAX_LENGTH);

    private static final Username DINNERBONE = new Username("Dinnerbone");
    private static final Username GRUMM = new Username("Grumm");
    private static final Username JEB = new Username("jeb_");

    /**
     * The unique ID of the player. When (extremely rarely) a player's UUID changes, the latest is used, and UUIDs
     * with invalid characters (this has happened before) are unsupported.
     */
    @NonNull UUID uuid;
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
     * @param profile Additional information about the player, null if PHD
     */
    public Player(@NonNull UUID uuid, @Nullable Profile profile) {
        this.uuid = uuid;
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
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a username");
        }
        return profile.getUsername();
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
     * @return The skin model of the player's current skin
     * @throws IllegalStateException If the player is PHD
     */
    public SkinModel getSkinModel() {
        if (profile == null) {
            throw new IllegalStateException("PHD accounts do not have a profile");
        }
        if (profile.getSkinUrl().isPresent()) {
            return profile.getSkinModel();
        }
        return getDefaultSkinModel();
    }
    /**
     * @return The default skin model according to the UUID
     */
    public SkinModel getDefaultSkinModel() {
        return SkinModel.defaultFor(uuid);
    }

    /**
     * @return The new 1.19.3+ default skin according to the UUID
     */
    public DefaultSkin getNewDefaultSkin() {
        return DefaultSkin.defaultFor(uuid);
    }

    /**
     * Checks if the player has a custom skin. Players who equip a default skin different from the one assigned to
     * their UUID count as having a custom skin.
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
        Optional<DefaultSkin> defaultSkinOpt = DefaultSkin.fromUrl(skinUrl);
        if (defaultSkinOpt.isPresent()) {
            DefaultSkin defaultSkin = defaultSkinOpt.get();
            return !getNewDefaultSkin().equals(defaultSkin);
        }
        return true;
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
        return profile.getSkinUrl().orElseGet(() -> getNewDefaultSkin().getURL());
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
        String encodedName = UrlUtils.encode(getUsername().toString());
        return UrlUtils.createUrl(String.format("http://s.optifine.net/capes/%s.png", encodedName));
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
        String encodedName = UrlUtils.encode(username.toString());
        return UrlUtils.createUrl("https://namemc.com/profile/" + encodedName);
    }
    /**
     * @param uuid The UUID of the player
     * @return A link to the player's NameMC profile
     */
    public static @NonNull URL getNameMCUrlFor(@NonNull UUID uuid) {
        return UrlUtils.createUrl("https://namemc.com/profile/" + uuid);
    }

    /**
     * @return A link to the player's MCSkinHistory profile
     */
    public @NonNull URL getMCSkinHistoryUrl() {
        return UrlUtils.createUrl("https://mcskinhistory.com/player/" + uuid);
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
        return "P:" + UuidUtils.toShortString(uuid) + "@" + requestTime.toEpochMilli();
    }

    private static Comparator<Player> initComparator() {
        return Comparator.comparing(Player::getUsername)
                .thenComparing(Player::getUuid)
                .thenComparing(Player::getRequestTime);
    }

}
