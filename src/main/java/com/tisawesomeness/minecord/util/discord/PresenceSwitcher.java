package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.config.branding.Presence;
import com.tisawesomeness.minecord.config.branding.PresenceConfig;
import com.tisawesomeness.minecord.util.Mth;

import lombok.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Switches between presences according to the {@link PresenceBehavior}.
 */
public class PresenceSwitcher {

    private final List<Presence> presences;
    private final int[] weights;
    private final @NonNull PresenceBehavior behavior;
    private int currentPresenceIndex;

    /**
     * Creates a new switcher.
     * @param config The config with the defined behavior
     */
    public PresenceSwitcher(PresenceConfig config) {
        presences = config.getPresences();
        weights = presences.stream()
                .mapToInt(Presence::getWeight)
                .toArray();
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
        currentPresenceIndex = (currentPresenceIndex + 1) % presences.size();
        return current();
    }
    public @NonNull Presence random() {
        currentPresenceIndex = Mth.weightedRandomIndex(weights);
        return current();
    }
    public @NonNull Presence randomUnique() {
        if (presences.size() > 1) {
            currentPresenceIndex = Mth.weightedRandomUniqueIndex(weights, currentPresenceIndex);
        }
        return current();
    }

    /**
     * @return The current presence
     */
    public @NonNull Presence current() {
        return presences.get(currentPresenceIndex);
    }

}
