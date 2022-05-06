package com.tisawesomeness.minecord.mc.player;

import com.tisawesomeness.minecord.lang.Localizable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a Minecraft account type, whether it's Microsoft, Minecraft, or legacy and how it was migrated.
 */
@RequiredArgsConstructor
public enum AccountStatus implements Localizable {
    NEW_MICROSOFT("new_msa"),
    MIGRATED_MICROSOFT("migrated_msa"),
    MIGRATED_MICROSOFT_FROM_LEGACY("migrated_msa_from_legacy"),
    UNKNOWN_MICROSOFT("msa"),
    MINECRAFT("mojang"),
    LEGACY("legacy");

    /**
     * The key that identifies the account status type according to the
     * <a href="https://api.gapple.pw/status/">Gapple API</a>
     * */
    @Getter private final String key;

    public @NonNull String getTranslationKey() {
        return "mc.player.accountStatus." + key;
    }
    public Object[] getTranslationArgs() {
        return new Object[0];
    }

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
