package com.tisawesomeness.minecord;

/**
 * The bot will exit with one of these exit codes depending on the reason.
 */
public final class ExitCodes {
    public static final int SUCCESS = 0;
    public static final int GENERAL_FAILURE = 1;
    // config errors
    public static final int COULD_NOT_CREATE_CONFIG = 10;
    public static final int CONFIG_IOE = 11;
    public static final int INVALID_CONFIG = 12;
    // init errors
    public static final int DATABASE_ERROR = 13;
    public static final int FAILED_TO_SET_OWNER = 14;
    // post-init errors
    public static final int VOTE_HANDLER_ERROR = 15;
}
