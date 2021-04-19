package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.mc.external.DualPlayerProvider;
import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.Getter;
import lombok.NonNull;

/**
 * Implements a MC Library using the standard implementations.
 */
public class StandardMCLibrary implements MCLibrary {

    @Getter private final @NonNull APIClient client;
    @Getter private final @NonNull PlayerProvider playerProvider;
    public StandardMCLibrary(@NonNull APIClient client, @NonNull Config config) {
        this.client = client;
        playerProvider = new DualPlayerProvider(client, config);
    }

}
