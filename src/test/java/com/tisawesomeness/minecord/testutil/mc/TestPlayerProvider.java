package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TestPlayerProvider implements PlayerProvider {

    private final Map<Username, UUID> uuidMap = new HashMap<>();
    public void mapUuid(@NonNull Username username, UUID uuid) {
        uuidMap.put(username, uuid);
    }
    private final Collection<Username> throwingUsernames = new ArrayList<>();
    public void throwOnUsername(@NonNull Username username) {
        throwingUsernames.add(username);
    }
    public CompletableFuture<Optional<UUID>> getUUID(@NonNull Username username) {
        if (throwingUsernames.contains(username)) {
            return failedFuture(new IOException("Mocked IOE"));
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(uuidMap.get(username)));
    }

    public CompletableFuture<Optional<Player>> getPlayer(@NonNull Username username) {
        if (throwingUsernames.contains(username)) {
            return failedFuture(new IOException("Mocked IOE"));
        }
        return getUUID(username).thenCompose(uuidOpt -> {
            if (!uuidOpt.isPresent()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return getPlayer(uuidOpt.get());
        });
    }

    private final Map<UUID, Player> playerMap = new HashMap<>();
    public void mapPlayer(@NonNull Player player) {
        playerMap.put(player.getUuid(), player);
    }
    private final Collection<UUID> throwingUuids = new ArrayList<>();
    public void throwOnUuid(@NonNull UUID uuid) {
        throwingUuids.add(uuid);
    }
    public CompletableFuture<Optional<Player>> getPlayer(@NonNull UUID uuid) {
        if (throwingUuids.contains(uuid)) {
            return failedFuture(new IOException("Mocked IOE"));
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(playerMap.get(uuid)));
    }

    private static <T> CompletableFuture<T> failedFuture(Throwable ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return future;
    }

}
