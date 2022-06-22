package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.external.ElectroidAPI;
import com.tisawesomeness.minecord.mc.player.Username;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MockElectroidAPI extends ElectroidAPI {

    private final Map<Username, String> usernameMap = new HashMap<>();

    public void mapUsername(@NonNull Username username, @NonNull String response) {
        usernameMap.put(username, response);
    }

    protected Optional<String> requestPlayer(@NonNull Username username) {
        return Optional.ofNullable(usernameMap.get(username));
    }

    private final Map<UUID, String> uuidMap = new HashMap<>();

    public void mapUuid(@NonNull UUID uuid, @NonNull String response) {
        uuidMap.put(uuid, response);
    }

    protected Optional<String> requestPlayer(@NonNull UUID uuid) {
        return Optional.ofNullable(uuidMap.get(uuid));
    }

}
