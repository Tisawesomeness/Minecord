package com.tisawesomeness.minecord.setting;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SetStatus implements SetResult {
    SET("{name} was changed from `{from}` to `{to}`.", true),
    SET_NO_CHANGE("{name} is already `{from}`.", true),
    SET_TO_DEFAULT("{name} was set to the default, `{to}`.", true),
    RESET("{name} was reset to `{to}`", true),
    RESET_NO_CHANGE("{name} is already the default, `{from}`", true),
    RESET_TO_DEFAULT("{name} was reset to the default, `{from}`.", true),
    INTERNAL_FAILURE("There was an internal error.", false),
    UNSUPPORTED("This operation is unsupported.", false);

    private final @NonNull String msg;
    public @NonNull String getMsg(String name, String from, String to) {
        return msg.replace("{name}", name).replace("{from}", from).replace("{to}", to);
    }
    @Getter private final boolean success;
}