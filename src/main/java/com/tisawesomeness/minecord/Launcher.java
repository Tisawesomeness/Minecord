package com.tisawesomeness.minecord;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

/**
 * Parses command-line args and starts up the bot.
 */
@Slf4j
public final class Launcher {

    /**
     * The entry point for Minecord.
     * @param args Processed by {@link ArgsHandler}
     */
    public static void main(String[] args) {
        log.debug("Program started");
        try {
            start(args);
        } catch (Exception ex) {
            log.error("FATAL: A fatal exception occurred on startup", ex);
            System.exit(ExitCodes.GENERAL_FAILURE);
        }
    }
    private static void start(String[] args) {
        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (exitCode != ExitCodes.SUCCESS) {
            System.exit(exitCode);
        }
        if (handle.isReady()) {
            int botExitCode = new Bot().setup(handle);
            if (botExitCode != ExitCodes.SUCCESS) {
                System.exit(botExitCode);
            }
        }
    }

}
