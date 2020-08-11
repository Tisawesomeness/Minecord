package com.tisawesomeness.minecord.command;

import com.tisawesomeness.minecord.Lang;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents the result of a command.
 */
@RequiredArgsConstructor
public enum Result {
    SUCCESS("Success", ":white_check_mark:"),
    WARNING("Warning", ":warning:"),
    ERROR("Error", ":x:"),
    EXCEPTION("Exception", ":boom:");

    @Getter private final @NonNull String name;
    @Getter private final @NonNull String emote;

    /**
     * Adds the emote associated with this Result to a message.
     * @param msg The message
     * @param lang The language to pull the template from
     * @return The modified message
     */
    public @NonNull String addEmote(CharSequence msg, Lang lang) {
        return lang.i18nf(formatKey("template"), msg, emote);
    }
    private @NonNull String formatKey(@NonNull String key) {
        return String.format("command.result.%s.%s", getId(), key);
    }
    private @NonNull String getId() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return name;
    }
}
