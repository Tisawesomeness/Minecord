package com.tisawesomeness.minecord.config;

import com.tisawesomeness.minecord.config.serial.PresenceConfig;
import com.tisawesomeness.minecord.config.serial.PresenceConfigEntry;

import lombok.NonNull;

import java.util.List;
import java.util.Random;

/**
 * Switches between presences according to the {@link PresenceBehavior}.
 */
public class PresenceSwitcher {

    private static final Random random = new Random();
    private final List<PresenceConfigEntry> presences;
    private final @NonNull PresenceBehavior behavior;
    private int currentPresence;

    /**
     * Creates a new switcher.
     * @param config The config with the defined behavior
     */
    public PresenceSwitcher(PresenceConfig config) {
        presences = config.getPresences();
        behavior = config.getBehavior();
    }

    /**
     * Switches the current presence.
     * @return The new presence
     */
    public @NonNull PresenceConfigEntry switchPresence() {
        return behavior.switchPresence(this);
    }

    public @NonNull PresenceConfigEntry cycle() {
        currentPresence = (currentPresence + 1) % presences.size();
        return current();
    }
    public @NonNull PresenceConfigEntry random() {
        currentPresence = random.nextInt(presences.size());
        return current();
    }
    public @NonNull PresenceConfigEntry randomUnique() {
        if (presences.size() == 1) {
            return current();
        }
        int r = random.nextInt(presences.size() - 1);
        if (r >= currentPresence) {
            r += 1;
        }
        currentPresence = r;
        return current();
    }

    private @NonNull PresenceConfigEntry current() {
        return presences.get(currentPresence);
    }

}
