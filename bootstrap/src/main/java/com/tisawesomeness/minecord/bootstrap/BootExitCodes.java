package com.tisawesomeness.minecord.bootstrap;

/**
 * The bootstrapper will exit with one of these exit codes depending on the reason.
 */
public final class BootExitCodes {
    public static final int SUCCESS = 0;
    public static final int GENERAL_FAILURE = 1;
    // args errors
    public static final int COULD_NOT_CREATE_FOLDER = 2;
    public static final int INVALID_PATH = 3;
    // config errors
    public static final int INSTANCE_CONFIG_CREATION_IOE = 4;
    public static final int INSTANCE_CONFIG_IOE = 5;
    public static final int INSTANCE_CONFIG_INVALID = 6;
    // init errors
    public static final int LOGIN_FAILURE = 7;
    public static final int LOGIN_IOE = 8;
    public static final int INTERRUPTED = 9;
}
