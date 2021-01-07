package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.external.MojangAPI;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MockMojangAPI extends MojangAPI {

    private final Map<Username, String> uuidMap = new HashMap<>();

    public void mapUUID(@NonNull Username username, @NonNull String response) {
        uuidMap.put(username, response);
    }

    protected Optional<String> requestUUID(@NonNull Username username) {
        return Optional.ofNullable(uuidMap.get(username));
    }

    private final Map<UUID, String> nameHistoryMap = new HashMap<>();

    public void mapNameHistory(@NonNull UUID uuid, @NonNull String response) {
        nameHistoryMap.put(uuid, response);
    }

    protected Optional<String> requestNameHistory(@NonNull UUID uuid) {
        return Optional.ofNullable(nameHistoryMap.get(uuid));
    }

    private final Map<UUID, String> profileMap = new HashMap<>();

    public void mapProfile(@NonNull UUID uuid, @NonNull String response) {
        profileMap.put(uuid, response);
    }

    protected Optional<String> requestProfile(@NonNull UUID uuid) {
        return Optional.ofNullable(profileMap.get(uuid));
    }

}
