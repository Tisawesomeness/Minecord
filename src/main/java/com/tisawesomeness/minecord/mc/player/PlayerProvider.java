package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.mc.MojangAPI;
import com.tisawesomeness.minecord.mc.MojangAPIImpl;
import com.tisawesomeness.minecord.network.APIClient;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * Requests and caches player data from the Mojang API
 */
@Slf4j
public class PlayerProvider {

    private final LoadingCache<Username, Optional<UUID>> uuidCache;
    private final LoadingCache<UUID, Optional<Player>> playerCache;
    private final MojangAPI api;

    /**
     * Creates a new PlayerProvider
     * @param client The API client to use for HTTP requests
     * @param flagConfig The flag config determines whether to record cache stats
     */
    public PlayerProvider(APIClient client, FlagConfig flagConfig) {
        api = new MojangAPIImpl(client);
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES);
        if (flagConfig.isDebugMode()) {
            builder.recordStats();
        }
        uuidCache = builder.build(this::loadUUID);
        playerCache = builder.build(this::loadPlayer);
    }

    private Optional<UUID> loadUUID(Username username) throws IOException {
        return api.getUUID(username);
    }

    private Optional<Player> loadPlayer(UUID uuid) throws IOException {
        List<NameChange> nameHistory = api.getNameHistory(uuid);
        if (nameHistory.isEmpty()) {
            return Optional.empty();
        }
        Username username = nameHistory.get(0).getUsername();
        Optional<Profile> profileOpt = api.getProfile(uuid);
        if (profileOpt.isEmpty()) {
            log.warn("Mojang API name history succeeded but profile failed. Check for a format change.");
            return Optional.empty();
        }
        Profile profile = profileOpt.get();
        return Optional.of(new Player(uuid, username, nameHistory, profile));
    }

    /**
     * Requests the UUID currently associated with the given username
     * @param username The input username
     * @return The associated UUID, or empty if the username doesn't currently exist
     * @throws IOException If an I/O error occurs
     */
    public Optional<UUID> getUUID(Username username) throws IOException {
        try {
            return uuidCache.get(username);
        } catch (CompletionException ex) {
            throwIfIOE(ex);
            throw ex;
        }
    }

    /**
     * Requests the player with the given username
     * @param username The input username
     * @return The player
     * @throws IOException If an I/O error occurs
     */
    public Optional<Player> getPlayer(Username username) throws IOException {
        Optional<UUID> uuidOpt = getUUID(username);
        if (uuidOpt.isEmpty()) {
            return Optional.empty();
        }
        return getPlayer(uuidOpt.get());
    }

    /**
     * Requests the player with the given UUID
     * @param uuid The input UUID
     * @return The player
     * @throws IOException If an I/O error occurs
     */
    public Optional<Player> getPlayer(UUID uuid) throws IOException {
        try {
            return playerCache.get(uuid);
        } catch (CompletionException ex) {
            throwIfIOE(ex);
            throw ex;
        }
    }

    private static void throwIfIOE(Throwable ex) throws IOException {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
            throw (IOException) cause;
        }
    }

}