package com.tisawesomeness.minecord.setting;

import com.tisawesomeness.minecord.util.type.Validation;

import lombok.NonNull;

public enum SetStatus {
    SET(true, ":white_check_mark: {name} was changed from `{from}` to `{to}`."),
    SET_NO_CHANGE(true, ":warning: {name} is already `{from}`."),
    SET_TO_DEFAULT(true, ":white_check_mark: {name} was set to the default, `{to}`."),
    SET_FROM_TO_DEFAULT(true, ":white_check_mark: {name} was changed from `{from}` to the default, `{to}`."),
    RESET(true, ":white_check_mark: {name} was reset to `{to}`"),
    RESET_NO_CHANGE(true, ":warning: {name} is already the default, `{from}`"),
    RESET_TO_DEFAULT(true, ":white_check_mark: {name} was reset to the default, `{from}`."),
    INTERNAL_FAILURE(false, ":x: There was an internal error."),
    UNSUPPORTED(false, ":x: This operation is unsupported.");

    private final Validation<String> validation;
    SetStatus(boolean isValid, @NonNull String msg) {
        validation = isValid ? Validation.valid(msg) : Validation.invalid(msg);
    }
    public @NonNull Validation<String> toValidation(String name, String from, String to) {
        return validation.map(s -> s
                .replace("{name}", name)
                .replace("{from}", from)
                .replace("{to}", to));
    }
}