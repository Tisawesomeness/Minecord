package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.mc.external.PlayerProvider;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.NonNull;

/**
 * A library for getting information about anything Minecraft-related.
 */
public interface MCLibrary {
    @NonNull APIClient getClient();
    @NonNull PlayerProvider getPlayerProvider();
}
