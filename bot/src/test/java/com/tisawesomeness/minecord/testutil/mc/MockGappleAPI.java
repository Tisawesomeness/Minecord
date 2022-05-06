package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.external.GappleAPI;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MockGappleAPI extends GappleAPI {

    private final Map<UUID, String> accountStatusMap = new HashMap<>();
    public void mapAccountStatus(@NonNull UUID uuid, @NonNull String status) {
        accountStatusMap.put(uuid, status);
    }
    protected Optional<String> requestAccountStatus(@NonNull UUID uuid) {
        return Optional.ofNullable(accountStatusMap.get(uuid));
    }

}
