package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.config.Config;
import com.tisawesomeness.minecord.config.PresenceSwitcher;

import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically changes the bot presence (game played) defined in config.
 */
public class PresenceService extends Service {
    private final @NonNull ShardManager sm;
    private final @NonNull Config config;
    private final @NonNull PresenceSwitcher switcher;

    /**
     * Initializes this service and the presence switcher.
     * @param sm The ShardManager used to switch presences
     * @param config The config containing all presences
     */
    public PresenceService(ShardManager sm, Config config) {
        this.sm = sm;
        this.config = config;
        switcher = new PresenceSwitcher(config);
    }

    @Override
    public boolean shouldRun() {
        return config.presenceChangeInterval > 0;
    }

    public void schedule(ScheduledExecutorService exe) {
        if (config.presences.size() == 1 && !config.presences.get(0).hasContent()) {
            exe.submit(this::run);
        } else {
            exe.scheduleAtFixedRate(this::run, 0, config.presenceChangeInterval, TimeUnit.SECONDS);
        }
    }

    /**
     * Call to manually run this service once.
     */
    public void run() {
        switcher.switchPresence().setPresence(sm);
    }
}
