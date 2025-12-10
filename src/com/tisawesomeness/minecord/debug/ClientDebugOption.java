package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.network.APIClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientDebugOption implements DebugOption {
    private final @NonNull APIClient client;
    public @NonNull String getName() {
        return "client";
    }
    public @NonNull String debug(@NonNull String extra) {
        return String.format("Calls: %d queued, %d running\nConnections: %d idle, %d total",
                client.getQueuedCallsCount(), client.getRunningCallsCount(),
                client.getIdleConnectionCount(), client.getConnectionCount());
    }
}
