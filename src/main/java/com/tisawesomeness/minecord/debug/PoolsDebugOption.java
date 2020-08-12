package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.command.CommandExecutor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PoolsDebugOption implements DebugOption {
    private final @NonNull CommandExecutor executor;
    public @NonNull String getName() {
        return "pools";
    }
    public @NonNull String debug(@NonNull String extra) {
        return executor.debugEstimatedSizes();
    }
}
