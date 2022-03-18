package com.tisawesomeness.minecord.mc.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a Minecraft account type, whether it's Microsoft, Minecraft, or legacy and how it was migrated.
 */
@RequiredArgsConstructor
public enum AccountStatus {
    NEW_MICROSOFT("new_msa", "New Microsoft Account"),
    MIGRATED_MICROSOFT("migrated_msa", "Migrated Microsoft Account"),
    MIGRATED_MICROSOFT_FROM_LEGACY("migrated_msa_from_legacy", "Microsoft from Legacy Account"),
    UNKNOWN_MICROSOFT("msa", "Microsoft Account"),
    MINECRAFT("mojang", "Minecraft Account"),
    LEGACY("legacy", "Legacy Account");

    /**
     * The key that identifies the account status type according to the
     * <a href="https://api.gapple.pw/status/">Gapple API</a>
     * */
    @Getter private final String key;
    @Getter private final String name;

    /**
     * Parses the account status from the key returned from the Gapple API.
     * @param key string key
     * @return the account status, or empty if not found
     */
    public static Optional<AccountStatus> from(@NonNull String key) {
        return Arrays.stream(values())
                .filter(status -> status.key.equalsIgnoreCase(key))
                .findFirst();
    }

}
