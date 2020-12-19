package com.tisawesomeness.minecord;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExitCode {
    SUCCESS(0),
    INVALID_PATH(1),
    CONFIG_IOE(2),
    ANNOUNCE_IOE(10),
    LOGIN_ERROR(11),
    DATABASE_ERROR(12),
    FAILED_TO_SET_OWNER(13),
    VOTE_HANDLER_ERROR(14);

    private final int code;
    public int asInt() {
        return code;
    }
}
