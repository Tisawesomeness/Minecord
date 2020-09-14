package com.tisawesomeness.minecord;

import picocli.CommandLine;

/**
 * Parses command-line args and starts up the bot.
 */
public final class Main {

    /**
     * The entry point for Minecord.
     * @param args Processed by {@link ArgsHandler}
     */
    public static void main(String[] args) {
        ArgsHandler handle = new ArgsHandler();
        int exitCode = new CommandLine(handle).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
        if (handle.isReady()) {
            int botExitCode = new Bot().setup(handle);
            if (botExitCode != 0) {
                System.exit(botExitCode);
            }
        }
    }

}
