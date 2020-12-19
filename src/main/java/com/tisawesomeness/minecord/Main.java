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
        if (exitCode != ExitCode.SUCCESS.asInt()) {
            System.exit(exitCode);
        }
        if (handle.isReady()) {
            ExitCode botExitCode = new Bot().setup(handle);
            if (botExitCode != ExitCode.SUCCESS) {
                System.exit(botExitCode.asInt());
            }
        }
    }

}
