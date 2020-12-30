package com.tisawesomeness.minecord;

import lombok.RequiredArgsConstructor;

/**
 * The bot will exit with one of these exit codes depending on the reason.
 */
@RequiredArgsConstructor
public enum ExitCode {
    SUCCESS(0),
    GENERAL_FAILURE(1),
    // pre-init errors
    COULD_NOT_CREATE_FOLDER(2),
    COULD_NOT_CREATE_CONFIG(3),
    INVALID_PATH(4),
    // init errors
    CONFIG_IOE(10),
    INVALID_CONFIG(11),
    ANNOUNCE_IOE(12),
    LOGIN_ERROR(13),
    DATABASE_ERROR(14),
    FAILED_TO_SET_OWNER(15),
    VOTE_HANDLER_ERROR(16);

    private final int code;
    /**
     * @return The exit code as an integer
     */
    public int asInt() {
        return code;
    }
}
