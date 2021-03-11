package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.config.serial.CacheConfig;
import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.config.serial.FlagConfig;
import com.tisawesomeness.minecord.mc.external.ElectroidAPI;
import com.tisawesomeness.minecord.mc.external.ElectroidAPIImpl;
import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.mc.external.MojangAPIImpl;
import com.tisawesomeness.minecord.network.APIClient;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Requests and caches player data from the Mojang API and the Electroid API wrapper (if enabled).
 */
@Slf4j
public class DualPlayerProvider implements PlayerProvider {

    private final AsyncLoadingCache<Username, Optional<UUID>> uuidCache;
    private final AsyncLoadingCache<UUID, Optional<Player>> playerCache;
    private final MojangAPI mojangAPI;
    private final ElectroidAPI electroidAPI;
    private final boolean useElectroidAPI;

    /**
     * Creates a new PlayerProvider
     * @param client The API client to use for HTTP requests
     * @param config The configuration to use for caching
     */
    public DualPlayerProvider(APIClient client, Config config) {
        FlagConfig flagConfig = config.getFlagConfig();
        CacheConfig cacheConfig = config.getCacheConfig();

        mojangAPI = new MojangAPIImpl(client);
        electroidAPI = new ElectroidAPIImpl(client);
        useElectroidAPI = flagConfig.isUseElectroidAPI();

        Caffeine<Object, Object> uuidBuilder = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfig.getMojangUuidLifetime(), TimeUnit.SECONDS);
        Caffeine<Object, Object> playerBuilder = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfig.getMojangPlayerLifetime(), TimeUnit.SECONDS);
        if (flagConfig.isDebugMode()) {
            uuidBuilder.recordStats();
            playerBuilder.recordStats();
        }
        uuidCache = uuidBuilder.buildAsync(this::loadUUID);
        playerCache = playerBuilder.buildAsync(this::loadPlayer);
    }

    private Optional<UUID> loadUUID(Username username) throws IOException {
        try {
            return mojangAPI.getUUID(username);
        } catch (IOException ex) {
            if (useElectroidAPI && username.isValid()) {
                log.warn("Getting UUID from Mojang API failed with IOE, trying Electroid API", ex);
                return tryUUIDFromElectroid(username);
            }
            log.error("Getting UUID from Mojang API failed with IOE, backup not available", ex);
            throw ex;
        }
    }
    private Optional<UUID> tryUUIDFromElectroid(Username username) throws IOException {
        Optional<Player> playerOpt = electroidAPI.getPlayer(username);
        if (!playerOpt.isPresent()) {
            return Optional.empty();
        }
        Player player = playerOpt.get();
        UUID uuid = player.getUuid();
        // since we have the player object, why not cache it?
        playerCache.asMap().putIfAbsent(uuid, CompletableFuture.completedFuture(playerOpt));
        return Optional.of(uuid);
    }

    private Optional<Player> loadPlayer(UUID uuid) throws IOException {
        if (useElectroidAPI) {
            try {
                return electroidAPI.getPlayer(uuid);
            } catch (IOException ex) {
                log.warn("Getting player from Electroid API failed with IOE, trying Mojang API", ex);
            }
        }
        return tryPlayerFromMojang(uuid);
    }
    private Optional<Player> tryPlayerFromMojang(UUID uuid) throws IOException {
        List<NameChange> nameHistory = mojangAPI.getNameHistory(uuid);
        if (nameHistory.isEmpty()) {
            return Optional.empty();
        }
        Username username = nameHistory.get(0).getUsername();
        Optional<Profile> profileOpt = mojangAPI.getProfile(uuid);
        if (!profileOpt.isPresent()) {
            log.warn("Mojang API name history succeeded but profile failed. Check for a format change.");
            return Optional.empty();
        }
        Profile profile = profileOpt.get();
        return Optional.of(new Player(uuid, username, nameHistory, profile));
    }

    public CompletableFuture<Optional<UUID>> getUUID(@NonNull Username username) {
        return uuidCache.get(username);
    }

    public CompletableFuture<Optional<Player>> getPlayer(@NonNull Username username) {
        return getUUID(username).thenCompose(uuidOpt -> {
            if (!uuidOpt.isPresent()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return getPlayer(uuidOpt.get());
        });
    }

    public CompletableFuture<Optional<Player>> getPlayer(@NonNull UUID uuid) {
        return playerCache.get(uuid);
    }

    // apparently async caches can't record stats? :(
    public CacheStats getUuidCacheStats() {
        return null;
    }
    public CacheStats getPlayerCacheStats() {
        return null;
    }

}
