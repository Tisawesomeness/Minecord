package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.Config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PresenceService extends Service {
    private final @NonNull ShardManager sm;
    private final @NonNull Config config;

    @Override
    public boolean shouldRun() {
        return config.presenceChangeInterval > 0;
    }

    public void schedule(ScheduledExecutorService exe) {
        exe.scheduleAtFixedRate(this::run, 0, config.presenceChangeInterval, TimeUnit.SECONDS);
    }

    public void run() {
        config.cyclePresence().setPresence(sm);
    }

}
