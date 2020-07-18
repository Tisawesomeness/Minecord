package com.tisawesomeness.minecord;

import lombok.NonNull;

import java.util.List;
import java.util.Random;

/**
 * Switches between presences according to the {@link PresenceBehavior}.
 */
public class PresenceSwitcher {

    private static final Random random = new Random();
    private final List<BotPresence> presences;
    private final @NonNull PresenceBehavior behavior;
    private int currentPresence;

    /**
     * Creates a new switcher.
     * @param config The config with the defined behavior
     */
    public PresenceSwitcher(Config config) {
        presences = config.presences;
        behavior = config.presenceBehavior;
    }

    /**
     * Switches the current presence.
     * @return The new presence
     */
    public @NonNull BotPresence switchPresence() {
        return behavior.switchPresence(this);
    }

    public @NonNull BotPresence cycle() {
        currentPresence = (currentPresence + 1) % presences.size();
        return current();
    }
    public @NonNull BotPresence random() {
        currentPresence = random.nextInt(presences.size());
        return current();
    }
    public @NonNull BotPresence randomUnique() {
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

    private @NonNull BotPresence current() {
        return presences.get(currentPresence);
    }

}
