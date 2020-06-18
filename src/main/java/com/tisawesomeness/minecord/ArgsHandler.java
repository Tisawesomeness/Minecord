package com.tisawesomeness.minecord;

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Uses picocli to make Minecord a command-line application.
 * Note: @Command is picocli's annotation, it does not represent a discord command.
 */
@Command(name = "minecord")
public class ArgsHandler implements Callable<Integer>, Serializable {

    @Option(names = {"-t", "--token"}, description = "The custom token to use.")
    @Getter private String tokenOverride;

    @Option(names = {"-p", "--path"}, description = "The path to the directory where config files are located. Can be overwritten with other arguments.")
    @Getter private Path path;

    @Option(names = {"-c", "-conf", "--config"}, description = "The path to the config file.")
    @Getter private Path configPath;

    @Override
    public Integer call() {

        // Default path is current directory, otherwise user specifies a directory
        if (path == null) {
            path = Paths.get(".");
        } else if (!Files.isDirectory(path)) {
            System.err.println("Path must be a directory!");
            return 1;
        }
        // config.json is the default config file
        if (configPath == null) {
            configPath = path.resolve("config.json");
        }

        return 0;

    }

}
