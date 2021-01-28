package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.network.APIClient;

import lombok.NonNull;

public class TestMCLibrary implements MCLibrary {
    private final TestPlayerProvider playerProvider = new TestPlayerProvider();

    public @NonNull APIClient getClient() {
        throw new UnsupportedOperationException("unsupported");
    }
    public @NonNull TestPlayerProvider getPlayerProvider() {
        return playerProvider;
    }

}
