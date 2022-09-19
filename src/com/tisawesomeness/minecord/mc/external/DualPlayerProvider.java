package com.tisawesomeness.minecord.mc.external;

import com.tisawesomeness.minecord.Config;
import com.tisawesomeness.minecord.mc.player.AccountStatus;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Profile;
import com.tisawesomeness.minecord.mc.player.Username;
import com.tisawesomeness.minecord.network.APIClient;
import com.tisawesomeness.minecord.util.type.ThrowingFunction;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Requests and caches player data from the Mojang API and the Electroid API wrapper (if enabled).
 */
public class DualPlayerProvider implements PlayerProvider {

    private final AsyncLoadingCache<Username, Optional<UUID>> uuidCache;
    private final AsyncLoadingCache<UUID, Optional<Player>> playerCache;
    // Present if gapple api enabled
    private final @Nullable AsyncLoadingCache<UUID, Optional<AccountStatus>> statusCache;
    // Present if electroid api enabled
    private final @Nullable CircuitBreaker<Object> electroidBreaker;
    // Present if gapple api enabled
    private final @Nullable CircuitBreaker<Object> gappleBreaker;

    private final MojangAPI mojangAPI;
    private final @Nullable ElectroidAPI electroidAPI;
    private final @Nullable GappleAPI gappleAPI;

    /**
     * Creates a new PlayerProvider
     * @param client The API client to use for HTTP requests
     */
    public DualPlayerProvider(@NonNull APIClient client) {
        boolean debugMode = Config.getDebugMode();

        mojangAPI = new MojangAPIImpl(client);
        electroidAPI = Config.getUseElectroidAPI() ? new ElectroidAPIImpl(client) : null;
        gappleAPI = Config.getUseGappleAPI() ? new GappleAPIImpl(client) : null;

        uuidCache = build(MojangAPI.PROFILE_RATELIMIT, debugMode, this::loadUUID);
        playerCache = build(MojangAPI.PROFILE_RATELIMIT, debugMode, this::loadPlayer);
        if (Config.getUseGappleAPI()) {
            statusCache = build(MojangAPI.PROFILE_RATELIMIT, debugMode, this::loadAccountStatus);
        } else {
            statusCache = null;
        }

        if (Config.getUseElectroidAPI()) {
            electroidBreaker = buildBreaker("Electroid API");
        } else {
            electroidBreaker = null;
        }
        if (Config.getUseGappleAPI()) {
            gappleBreaker = buildBreaker("Gapple API");
        } else {
            gappleBreaker = null;
        }
    }

    @Contract("null, _, _ -> null")
    private static @Nullable <T, R> AsyncLoadingCache<T, R> build(@Nullable Integer lifetime, boolean debugMode,
            @NonNull ThrowingFunction<? super T, ? extends R, IOException> loadingFunc) {
        if (lifetime == null) {
            return null;
        }
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(lifetime));
        if (debugMode) {
            builder.recordStats();
        }
        return builder.buildAsync(loadingFunc::apply);
    }
    private static CircuitBreaker<Object> buildBreaker(@NonNull String name) {
        return CircuitBreaker.builder()
                .handle(IOException.class)
                .withFailureRateThreshold(20, 10, Duration.ofSeconds(100))
                .withDelay(Duration.ofSeconds(300))
                .withSuccessThreshold(5)
                .onOpen(e -> System.out.printf("WARN: %s circuit breaker tripped from state %s\n", name, e.getPreviousState()))
                .onHalfOpen(e -> System.out.println(name + " circuit breaker half-open"))
                .onClose(e -> System.out.println(name + " circuit breaker reset"))
                .build();
    }

    private Optional<UUID> loadUUID(Username username) throws IOException {
        try {
            return mojangAPI.getUUID(username);
        } catch (IOException ex) {
            if (Config.getUseElectroidAPI() && username.isValid()) {
                System.out.println("WARN: Getting UUID from Mojang API failed with IOE, trying Electroid API");
                ex.printStackTrace();
                return tryUUIDFromElectroid(username);
            }
            System.err.println("Getting UUID from Mojang API failed with IOE, backup not available");
            throw ex;
        }
    }
    private Optional<UUID> tryUUIDFromElectroid(Username username) throws IOException {
        Optional<Player> playerOpt;
        try {
            Objects.requireNonNull(electroidBreaker);
            Objects.requireNonNull(electroidAPI);
            playerOpt = Failsafe.with(electroidBreaker).get(() -> electroidAPI.getPlayer(username));
        } catch (FailsafeException ex) {
            return handleFailsafe(ex);
        }
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
        if (Config.getUseElectroidAPI()) {
            try {
                Objects.requireNonNull(electroidBreaker);
                Objects.requireNonNull(electroidAPI);
                return Failsafe.with(electroidBreaker).get(() -> electroidAPI.getPlayer(uuid));
            } catch (Exception ex) {
                System.out.println("WARN: Getting player from Electroid API failed with IOE, trying Mojang API");
                ex.printStackTrace();
            }
        }
        return tryPlayerFromMojang(uuid);
    }
    private final ThrowingFunction<UUID, Optional<Player>, IOException> loadFromMojang = this::tryPlayerFromMojang;
    private Optional<Player> tryPlayerFromMojang(UUID uuid) throws IOException {
        Optional<Profile> profileOpt = mojangAPI.getProfile(uuid);
        if (!profileOpt.isPresent()) {
            System.out.printf("Account %s has no profile, PHD account found\n", uuid);
        }
        Profile profile = profileOpt.orElse(null);
        return Optional.of(new Player(uuid, profile));
    }

    private Optional<AccountStatus> loadAccountStatus(UUID uuid) throws IOException {
        try {
            return Objects.requireNonNull(gappleAPI).getAccountStatus(uuid);
        } catch (IOException ex) {
            System.out.println("WARN: Getting account status from Gapple API failed with IOE");
            throw ex;
        }
    }

    public boolean isStatusAPIEnabled() {
        return Config.getUseGappleAPI() && !Objects.requireNonNull(gappleBreaker).isOpen();
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
        if (statusCache == null) {
            throw new IllegalStateException("Gapple API is not enabled");
        }
        return Failsafe.with(Objects.requireNonNull(gappleBreaker)).getStageAsync(() -> statusCache.get(uuid))
                .exceptionally(DualPlayerProvider::handleFailsafe);
    }

    @SneakyThrows // rethrow checked without wrapping in RuntimeException
    private static <T> T handleFailsafe(Throwable ex) {
        if (ex instanceof FailsafeException) {
            throw ex.getCause(); // Usually IOE
        }
        throw ex;
    }

}
