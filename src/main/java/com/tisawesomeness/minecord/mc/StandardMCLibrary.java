package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.mc.player.DualPlayerProvider;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.Getter;

/**
 * Implements a MC Library using the standard implementations.
 */
public class StandardMCLibrary implements MCLibrary {
    @Getter private final PlayerProvider playerProvider;

    public StandardMCLibrary(APIClient client, Config config) {
        playerProvider = new DualPlayerProvider(client, config.getFlagConfig());
    }
}
