package com.tisawesomeness.minecord.command.runner.mc;

import com.tisawesomeness.minecord.mc.player.Player;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;

import java.io.IOException;
import java.util.*;

public class TestPlayerProvider implements PlayerProvider {

    private final Map<Username, UUID> uuidMap = new HashMap<>();
    public void mapUuid(@NonNull Username username, UUID uuid) {
        uuidMap.put(username, uuid);
    }
    private final Collection<Username> throwingUsernames = new ArrayList<>();
    public void throwOnUsername(@NonNull Username username) {
        throwingUsernames.add(username);
    }
    public Optional<UUID> getUUID(@NonNull Username username) throws IOException {
        if (throwingUsernames.contains(username)) {
            throw new IOException("Mocked IOE");
        }
        return Optional.ofNullable(uuidMap.get(username));
    }

    public Optional<Player> getPlayer(@NonNull Username username) throws IOException {
        if (throwingUsernames.contains(username)) {
            throw new IOException("Mocked IOE");
        }
        Optional<UUID> uuidOpt = getUUID(username);
        if (uuidOpt.isEmpty()) {
            return Optional.empty();
        }
        return getPlayer(uuidOpt.get());
    }

    private final Map<UUID, Player> playerMap = new HashMap<>();
    public void mapPlayer(@NonNull UUID uuid, @NonNull Player player) {
        playerMap.put(uuid, player);
    }
    private final Collection<UUID> throwingUuids = new ArrayList<>();
    public void throwOnUuid(@NonNull UUID uuid) {
        throwingUuids.add(uuid);
    }
    public Optional<Player> getPlayer(@NonNull UUID uuid) throws IOException {
        if (throwingUuids.contains(uuid)) {
            throw new IOException("Mocked IOE");
        }
        return Optional.ofNullable(playerMap.get(uuid));
    }

}
