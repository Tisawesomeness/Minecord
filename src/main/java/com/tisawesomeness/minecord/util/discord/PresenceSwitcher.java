package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.config.branding.Presence;
import com.tisawesomeness.minecord.config.branding.PresenceConfig;

import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Switches between presences according to the {@link PresenceBehavior}.
 */
public class PresenceSwitcher {

    private final List<Presence> presences;
    private final @NonNull PresenceBehavior behavior;
    private int currentPresence;

    /**
     * Creates a new switcher.
     * @param config The config with the defined behavior
     */
    public PresenceSwitcher(PresenceConfig config) {
        presences = config.getPresences();
        behavior = Objects.requireNonNull(config.getBehavior());
    }

    /**
     * Switches the current presence.
     * @return The new presence
     */
    public @NonNull Presence switchPresence() {
        return behavior.switchPresence(this);
    }

    public @NonNull Presence cycle() {
        currentPresence = (currentPresence + 1) % presences.size();
        return current();
    }
    public @NonNull Presence random() {
        currentPresence = ThreadLocalRandom.current().nextInt(presences.size());
        return current();
    }
    public @NonNull Presence randomUnique() {
        if (presences.size() == 1) {
            return current();
        }
        int r = ThreadLocalRandom.current().nextInt(presences.size() - 1);
        if (r >= currentPresence) {
            r += 1;
        }
        currentPresence = r;
        return current();
    }

    /**
     * @return The current presence
     */
    public @NonNull Presence current() {
        return presences.get(currentPresence);
    }

}
