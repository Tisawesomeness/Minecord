package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.config.config.FlagConfig;
import com.tisawesomeness.minecord.config.config.MojangAPIConfig;
import com.tisawesomeness.minecord.mc.player.*;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.util.type.ThrowingFunction;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Requests and caches player data from the Mojang API and the Electroid API wrapper (if enabled).
 */
@Slf4j
public class DualPlayerProvider implements PlayerProvider {

    private final AsyncLoadingCache<Username, Optional<UUID>> uuidCache;
    private final AsyncLoadingCache<UUID, Optional<Player>> playerCache;
    private final @Nullable AsyncLoadingCache<UUID, Optional<AccountStatus>> statusCache;
    private final MojangAPI mojangAPI;
    private final ElectroidAPI electroidAPI;
    private final @Nullable GappleAPI gappleAPI;
    private final boolean useElectroidAPI;
    private final boolean useGappleAPI;

    /**
     * Creates a new PlayerProvider
     * @param client The API client to use for HTTP requests
     * @param config The configuration to use for caching
     */
    public DualPlayerProvider(@NonNull APIClient client, @NonNull Config config) {
        FlagConfig flagConfig = config.getFlagConfig();
        MojangAPIConfig mConfig = config.getAdvancedConfig().getMojangAPIConfig();
        boolean debugMode = flagConfig.isDebugMode();

        useElectroidAPI = flagConfig.isUseElectroidAPI();
        useGappleAPI = flagConfig.isUseGappleAPI();

        mojangAPI = new MojangAPIImpl(client);
        electroidAPI = new ElectroidAPIImpl(client);
        if (useGappleAPI) {
            gappleAPI = new GappleAPIImpl(client);
        } else {
            gappleAPI = null;
        }

        uuidCache = build(mConfig.getMojangUuidLifetime(), debugMode, this::loadUUID);
        playerCache = build(mConfig.getMojangPlayerLifetime(), debugMode, this::loadPlayer);
        if (useGappleAPI) {
            statusCache = build(mConfig.getGappleStatusLifetime(), debugMode, this::loadAccountStatus);
        } else {
            statusCache = null;
        }
    }
    private static <T, R> AsyncLoadingCache<T, R> build(int lifetime, boolean debugMode,
                ThrowingFunction<? super T, ? extends R, IOException> loadingFunc) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(lifetime));
        if (debugMode) {
            builder.recordStats();
        }
        return builder.buildAsync(loadingFunc::apply);
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
    private final ThrowingFunction<UUID, Optional<Player>, IOException> loadFromMojang = this::tryPlayerFromMojang;
    private Optional<Player> tryPlayerFromMojang(UUID uuid) throws IOException {
        List<NameChange> nameHistory = mojangAPI.getNameHistory(uuid);
        if (nameHistory.isEmpty()) {
            return Optional.empty();
        }
        Optional<Profile> profileOpt = mojangAPI.getProfile(uuid);
        if (!profileOpt.isPresent()) {
            log.info("Account {} has no profile, PHD account found", uuid);
        }
        Profile profile = profileOpt.orElse(null);
        return Optional.of(new Player(uuid, nameHistory, profile));
    }

    private Optional<AccountStatus> loadAccountStatus(UUID uuid) throws IOException {
        try {
            return Objects.requireNonNull(gappleAPI).getAccountStatus(uuid);
        } catch (IOException ex) {
            log.warn("Getting account status from Gapple API failed with IOE", ex);
            throw ex;
        }
    }

    public boolean isStatusAPIEnabled() {
        return useGappleAPI;
    }

    public CompletableFuture<Optional<UUID>> getUUID(@NonNull Username username) {
        return uuidCache.get(username);
    }

    public CompletableFuture<Optional<Player>> getPlayer(@NonNull Username username) {
        return getUUID(username).thenCompose(this::getPlayerIfPresent);
    }
    private CompletableFuture<Optional<Player>> getPlayerIfPresent(Optional<UUID> uuidOpt) {
        if (!uuidOpt.isPresent()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return playerCache.get(uuidOpt.get());
    }
    public CompletableFuture<Optional<Player>> getPlayer(@NonNull UUID uuid) {
        return playerCache.get(uuid, loadFromMojang);
    }

    public CompletableFuture<Optional<AccountStatus>> getAccountStatus(@NonNull UUID uuid) {
        if (statusCache != null) {
            return statusCache.get(uuid);
        }
        throw new IllegalStateException("Gapple API is not enabled");
    }

    public @NonNull CacheStats getUuidCacheStats() {
        return uuidCache.synchronous().stats();
    }
    public @NonNull CacheStats getPlayerCacheStats() {
        return playerCache.synchronous().stats();
    }
    public @Nullable CacheStats getStatusCacheStats() {
        return statusCache == null ? null : statusCache.synchronous().stats();
    }

}
