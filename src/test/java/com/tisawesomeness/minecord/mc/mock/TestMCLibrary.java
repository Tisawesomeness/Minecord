package com.tisawesomeness.minecord.mc.mock;

import com.tisawesomeness.minecord.mc.MCLibrary;

import lombok.NonNull;

public class TestMCLibrary implements MCLibrary {
    private final TestPlayerProvider playerProvider = new TestPlayerProvider();
    public @NonNull TestPlayerProvider getPlayerProvider() {
        return playerProvider;
    }
}
