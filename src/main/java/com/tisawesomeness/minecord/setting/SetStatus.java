package com.tisawesomeness.minecord.setting;

/**
 * An enum representing the possible outcomes of changing a setting.
 */
public enum SetStatus {
    SET(),
    SET_NO_CHANGE(),
    UNSET_TO_DEFAULT(),
    RESET(),
    RESET_NO_CHANGE(),
    RESET_TO_DEFAULT(),
    INVALID_INPUT(),
    INTERNAL_FAILURE()
}
