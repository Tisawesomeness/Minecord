package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.IO;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Uses picocli to make Minecord a command-line application.
 * Note: @Command is picocli's annotation, it does not represent a discord command.
 */
@Command(
        name = "minecord",
        mixinStandardHelpOptions = true,
        versionProvider = BuildInfo.VersionProvider.class
)
@Slf4j
public class ArgsHandler implements Callable<Integer>, Serializable {

    @Option(names = {"-p", "--path"}, description = "The path to the directory where config files are located.")
    @Getter private @Nullable Path path;

    /**
     * Whether the bot should be started
     */
    @Getter private boolean ready;
    private @Nullable Path configPath;
    private @Nullable Path brandingPath;

    /**
     * Parses all command-line arguments.
     * @return {@code 0} for success, {@code 1} for user failure, {@code 2} for fatal exception
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
                    return ExitCodes.COULD_NOT_CREATE_FOLDER;
                }
            }
        } else if (!path.toFile().isDirectory()) {
            log.error("FATAL: The path argument must be a directory!");
            return ExitCodes.INVALID_PATH;
        }

        // ensure branding exists, but it's not necessary
        brandingPath = path.resolve("branding.yml");
        if (!brandingPath.toFile().exists()) {
            log.debug("Branding file does not exist, creating...");
            createBranding(brandingPath);
        }

        // config, however, is necessary
        if (configPath == null) {
            configPath = path.resolve("config.yml");
        }
        if (!configPath.toFile().exists()) {
            return createConfig(configPath);
        }

        ready = true;
        log.info("Found valid config file");
        return ExitCodes.SUCCESS;

    }


    private static int createConfig(Path configPath) {
        log.debug("Creating config...");
        try {
            IO.write(configPath, IO.loadResource("config.yml"));
            log.info("The config file was created! Put your bot token in config.yml to run the bot.");
        } catch (IOException ex) {
            log.error("FATAL: There was an error creating the config", ex);
            return ExitCodes.COULD_NOT_CREATE_CONFIG;
        }
        return ExitCodes.SUCCESS;
    }

    private static void createBranding(Path brandingPath) {
        try {
            IO.write(brandingPath, IO.loadResource("branding.yml"));
        } catch (IOException ex) {
            log.warn("Could not load branding file, continuing anyway...", ex);
        }
    }

    /**
     * @return The path to the config file
     * @throws IllegalArgumentException if {@link #isReady()} is false
     */
    public @NonNull Path getConfigPath() {
        if (configPath == null) {
            throw new IllegalStateException("Args handler is not ready!");
        }
        return configPath;
    }
    /**
     * @return The path to the branding config file, may not exist
     * @throws IllegalArgumentException if {@link #isReady()} is false
     */
    public Optional<Path> getBrandingPath() {
        if (!ready) {
            throw new IllegalStateException("Args handler is not ready!");
        }
        return Optional.ofNullable(brandingPath);
    }

}

