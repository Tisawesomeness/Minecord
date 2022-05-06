package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Requests player data from a UUID or username.
 */
public interface PlayerProvider {

    /**
     * Requests the UUID currently associated with the given username.
     * <br>The future throws {@link IOException} If an I/O error occurs
     * @param username The input username
     * @return The associated UUID, or empty if the username doesn't currently exist
     */
    CompletableFuture<Optional<UUID>> getUUID(@NonNull Username username);

    /**
     * Requests the player with the given username.
     * <br>The future throws {@link IOException} If an I/O error occurs
     * @param username The input username
     * @return The player, or empty if the username doesn't currently exist
     */
    CompletableFuture<Optional<Player>> getPlayer(@NonNull Username username);

    /**
     * Requests the player with the given UUID.
     * <br>The future throws {@link IOException} If an I/O error occurs
     * @param uuid The input UUID
     * @return The player, or empty if the UUID doesn't currently exist
     */
    CompletableFuture<Optional<Player>> getPlayer(@NonNull UUID uuid);

    /**
     * @return true if getAccountStatus() is enabled
     */
    boolean isStatusAPIEnabled();

    /**
     * Requests the status of the account associated with the given UUID.
     * <br>The future throws {@link IOException} If an I/O error occurs
     * @param uuid a valid UUID
     * @return the account status, or empty if the uuid doesn't <b>currently</b> exist
     * @throws IllegalStateException if status APIs are disabled
     */
    CompletableFuture<Optional<AccountStatus>> getAccountStatus(@NonNull UUID uuid);

}
