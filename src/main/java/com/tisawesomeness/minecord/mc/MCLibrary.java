package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.config.serial.Config;
import com.tisawesomeness.minecord.mc.player.PlayerProvider;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.Getter;

public class MCLibrary {
    @Getter private final PlayerProvider playerProvider;

    public MCLibrary(APIClient client, Config config) {
        playerProvider = new PlayerProvider(client, config.getFlagConfig());
    }
}
