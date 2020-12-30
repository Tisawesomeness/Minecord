package com.tisawesomeness.minecord.mc.player;

import lombok.NonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Requests player data from a UUID or username.
 */
public interface PlayerProvider {

    /**
     * Requests the UUID currently associated with the given username
     * @param username The input username
     * @return The associated UUID, or empty if the username doesn't currently exist
     * @throws IOException If an I/O error occurs
     */
    Optional<UUID> getUUID(@NonNull Username username) throws IOException;

    /**
     * Requests the player with the given username
     * @param username The input username
     * @return The player, or empty if the username doesn't currently exist
     * @throws IOException If an I/O error occurs
     */
    Optional<Player> getPlayer(@NonNull Username username) throws IOException;

    /**
     * Requests the player with the given UUID
     * @param uuid The input UUID
     * @return The player, or empty if the UUID doesn't currently exist
     * @throws IOException If an I/O error occurs
     */
    Optional<Player> getPlayer(@NonNull UUID uuid) throws IOException;

}
