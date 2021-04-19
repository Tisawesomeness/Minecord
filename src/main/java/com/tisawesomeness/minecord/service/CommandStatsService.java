package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.command.CommandExecutor;
import com.tisawesomeness.minecord.config.config.CommandConfig;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CommandStatsService extends Service {
    private final @NonNull CommandExecutor executor;
    private final @NonNull CommandConfig cc;

    public void schedule(ScheduledExecutorService exe) {
        int interval = cc.getPushUsesInterval();
        exe.scheduleAtFixedRate(this::run, interval, interval, TimeUnit.SECONDS);
    }
    private void run() {
        executor.pushUses();
    }
}
