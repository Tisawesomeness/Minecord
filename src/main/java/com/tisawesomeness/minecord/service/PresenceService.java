package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.Placeholders;
import com.tisawesomeness.minecord.config.branding.Branding;
import com.tisawesomeness.minecord.config.branding.Presence;
import com.tisawesomeness.minecord.config.branding.PresenceConfig;
import com.tisawesomeness.minecord.util.discord.PresenceSwitcher;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.annotation.Nullable;
import java.util.Objects;
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
        Presence presence = switcher.switchPresence();
        if (!presence.hasContent()) {
            sm.setPresence(presence.getStatus(), null);
            return;
        }
        String parsedContent = Placeholders.parseVariables(Objects.requireNonNull(presence.getContent()), sm);
        String displayContent = parsedContent.substring(Presence.MAX_CONTENT_LENGTH);
        Activity.ActivityType activityType = Objects.requireNonNull(presence.getType()).getActivityType();
        Activity jdaActivity = Activity.of(activityType, displayContent, presence.getUrl());
        sm.setPresence(presence.getStatus(), jdaActivity);
    }
}
