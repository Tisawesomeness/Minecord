package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.common.BuildInfo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Uses picocli to make Minecord a command-line application.
 * Note: @Command is picocli's annotation, it does not represent a discord command.
 */
@Command(
        name = "minecord",
        mixinStandardHelpOptions = true,
        versionProvider = ArgsHandler.VersionProvider.class
)
@Slf4j
public final class ArgsHandler implements Callable<Integer>, Serializable {

    @Option(names = {"-p", "--path"}, description = "The path to the directory where config files are located.")
    @Getter private @Nullable Path path;

    /**
     * Resolves the path command line argument.
     * If path is not specified, ./minecord is used as the default.
     * If the path does not exist, a directory is created.
     * @return an exit code, {@link BootExitCodes#SUCCESS} if the path directory was found or created
     */
    @Override
    public Integer call() {
        // Path is null if not specified by user
        if (path == null) {
            path = Paths.get("./minecord");
            if (!path.toFile().exists()) {
                log.debug("No path argument was provided and default folder does not exist, creating...");
                try {
                    Files.createDirectory(path);
                } catch (IOException ex) {
                    log.error("FATAL: There was an error creating the minecord folder", ex);
                    return BootExitCodes.COULD_NOT_CREATE_FOLDER;
                }
            }
        } else if (!path.toFile().isDirectory()) {
            log.error("FATAL: The path argument must be a directory!");
            return BootExitCodes.INVALID_PATH;
        }
        return BootExitCodes.SUCCESS;
    }

    protected final static class VersionProvider implements CommandLine.IVersionProvider {
        public String[] getVersion() {
            return new String[]{"Minecord " + BuildInfo.getInstance().version};
        }
    }

}
