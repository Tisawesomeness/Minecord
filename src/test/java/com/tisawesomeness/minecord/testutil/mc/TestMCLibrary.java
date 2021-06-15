package com.tisawesomeness.minecord.testutil.mc;

import com.tisawesomeness.minecord.mc.MCLibrary;
import com.tisawesomeness.minecord.testutil.network.TestClient;

import lombok.Getter;
import lombok.NonNull;

public class TestMCLibrary implements MCLibrary {
    @Getter private final @NonNull TestClient client = new TestClient();
    @Getter private final @NonNull TestPlayerProvider playerProvider = new TestPlayerProvider();
}
