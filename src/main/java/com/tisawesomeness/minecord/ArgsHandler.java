package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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
        versionProvider = BuildInfo.VersionProvider.class
)
@Slf4j
public class ArgsHandler implements Callable<Integer>, Serializable {

    @Option(names = {"-t", "--token"}, description = "The custom token to use.")
    @Getter private String tokenOverride;

    @Option(names = {"-p", "--path"}, description = "The path to the directory where config files are located. Can be overwritten with other arguments.")
    @Getter private Path path;

    @Option(names = {"-c", "-conf", "--config"}, description = "The path to the config file.")
    @Getter private Path configPath;

    @Option(names = {"-a", "-announce", "--announcements"}, description = "The path to the announcements file.")
    @Getter private Path announcePath;

    /**
     * Whether the bot should be started
     */
    @Getter private boolean ready;

    /**
     * Parses all command-line arguments.
     * @return {@code 0} for success, {@code 1} for user failure, {@code 2} for fatal exception
     */
    @Override
    public Integer call() {
        return handle().asInt();
    }

    private ExitCode handle() {

        // Path is null if not specified by user
        if (path == null) {
            path = Paths.get("./minecord");
            if (!path.toFile().exists()) {
                log.debug("No path argument was provided and default folder does not exist, creating...");
                try {
                    Files.createDirectory(path);
                } catch (IOException ex) {
                    log.error("FATAL: There was an error creating the minecord folder", ex);
                    return ExitCode.COULD_NOT_CREATE_FOLDER;
                }
            }
        } else if (!path.toFile().isDirectory()) {
            log.error("FATAL: The path argument must be a directory!");
            return ExitCode.INVALID_PATH;
        }

        // ensure announce exists, but it's not necessary
        if (announcePath == null) {
            announcePath = path.resolve("announce.json");
        }
        if (!announcePath.toFile().exists()) {
            log.debug("Announce file does not exist, creating...");
            createAnnounce(announcePath);
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
        return ExitCode.SUCCESS;

    }

    private static ExitCode createConfig(Path configPath) {
        log.debug("Creating config...");
        try {
            Files.writeString(configPath, RequestUtils.loadResource("config.yml"));
            log.info("The config file was created! Put your bot token in config.yml to run the bot.");
        } catch (IOException ex) {
            log.error("FATAL: There was an error creating the config", ex);
            return ExitCode.COULD_NOT_CREATE_CONFIG;
        }
        return ExitCode.SUCCESS;
    }

    private static void createAnnounce(Path announcePath) {
        try {
            Files.writeString(announcePath, RequestUtils.loadResource("announce.json"));
        } catch (IOException ex) {
            log.warn("Could not load announce file, continuing anyway...", ex);
        }
    }

}

