package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.mc.player.PlayerProvider;

import lombok.NonNull;

/**
 * A library for getting information about anything Minecraft-related.
 */
public interface MCLibrary {
    @NonNull PlayerProvider getPlayerProvider();
}
