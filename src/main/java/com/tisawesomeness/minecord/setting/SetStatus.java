package com.tisawesomeness.minecord.setting;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SetStatus implements SetResult {
    SET(":white_check_mark: {name} was changed from `{from}` to `{to}`.", true),
    SET_NO_CHANGE(":warning: {name} is already `{from}`.", true),
    SET_TO_DEFAULT(":white_check_mark: {name} was set to the default, `{to}`.", true),
    SET_FROM_TO_DEFAULT(":white_check_mark: {name} was changed from `{from}` to the default, `{to}`.", true),
    RESET(":white_check_mark: {name} was reset to `{to}`", true),
    RESET_NO_CHANGE(":warning: {name} is already the default, `{from}`", true),
    RESET_TO_DEFAULT(":white_check_mark: {name} was reset to the default, `{from}`.", true),
    INTERNAL_FAILURE(":x: There was an internal error.", false),
    UNSUPPORTED(":x: This operation is unsupported.", false);

    private final @NonNull String msg;
    public @NonNull String getMsg(String name, String from, String to) {
        return msg.replace("{name}", name).replace("{from}", from).replace("{to}", to);
    }
    @Getter private final boolean success;
}