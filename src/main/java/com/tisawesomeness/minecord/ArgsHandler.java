package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.Getter;
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

        // Path is null if not specified by user
        if (path == null) {
            path = Paths.get("./minecord");
            if (!path.toFile().exists()) {
                try {
                    Files.createDirectory(path);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return 2;
                }
            }
        } else if (!path.toFile().isDirectory()) {
            System.err.println("Path must be a directory!");
            return 1;
        }

        // ensure announce exists, but it's not necessary
        if (announcePath == null) {
            announcePath = path.resolve("announce.json");
        }
        if (!announcePath.toFile().exists()) {
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
        return 0;

    }

    private static int createConfig(Path configPath) {
        try {
            Files.write(configPath, RequestUtils.loadResource("config.yml").getBytes());
            System.out.println("The config file was created! Put your bot token in config.yml to run the bot.");
        } catch (IOException ex) {
            ex.printStackTrace();
            return 2;
        }
        return 0;
    }

    private static void createAnnounce(Path announcePath) {
        try {
            Files.write(announcePath, RequestUtils.loadResource("announce.json").getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

