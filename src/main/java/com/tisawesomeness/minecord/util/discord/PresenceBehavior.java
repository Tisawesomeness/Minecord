package com.tisawesomeness.minecord.util.discord;

import com.tisawesomeness.minecord.config.branding.Presence;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * Decides how the {@link PresenceSwitcher} switches between presences.
 */
@RequiredArgsConstructor
public enum PresenceBehavior {
    /**
     * Cycles through all possible presences in a fixed order.
     */
    CYCLE(PresenceSwitcher::cycle),
    /**
     * Randomly picks a presence, sometimes picking the same one twice in a row.
     */
    RANDOM(PresenceSwitcher::random),
    /**
     * Randomly picks a presence, but never the same one twice in a row (unless there is only one).
     */
    RANDOM_UNIQUE(PresenceSwitcher::randomUnique);

    private final @NonNull Function<PresenceSwitcher, Presence> switchFunction;

    /**
     * Switches the current presence of the switcher
     * @param switcher The switcher to be modified
     * @return The new presence
     */
    public @NonNull Presence switchPresence(PresenceSwitcher switcher) {
        return switchFunction.apply(switcher);
    }

}
