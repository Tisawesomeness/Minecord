package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.branding.PresenceConfig;
import com.tisawesomeness.minecord.config.branding.PresenceSwitcher;

import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically changes the bot presence (game played) defined in config.
 */
public class PresenceService extends Service {
    private final @NonNull ShardManager sm;
    private final @Nullable PresenceConfig config;
    private final @Nullable PresenceSwitcher switcher;

    /**
     * Initializes this service and the presence switcher.
     * @param sm The ShardManager used to switch presences
     * @param branding The branding config containing all presences
     */
    public PresenceService(@NonNull ShardManager sm, @Nullable Branding branding) {
        this.sm = sm;
        if (branding == null) {
            config = null;
            switcher = null;
        } else {
            config = branding.getPresenceConfig();
            switcher = new PresenceSwitcher(config);
        }
    }

    @Override
    public boolean shouldRun() {
        return config != null && config.getChangeInterval() > 0;
    }

    public void schedule(ScheduledExecutorService exe) {
        if (config.getPresences().size() == 1 && !config.getPresences().get(0).hasContent()) {
            exe.submit(this::run);
        } else {
            exe.scheduleAtFixedRate(this::run, 0, config.getChangeInterval(), TimeUnit.SECONDS);
        }
    }

    /**
     * Call to manually run this service once.
     */
    public void run() {
        switcher.switchPresence().setPresence(sm);
    }
}
