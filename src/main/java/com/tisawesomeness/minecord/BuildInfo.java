package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.IO;

import lombok.Getter;
import lombok.NonNull;
import picocli.CommandLine;

import java.util.Properties;

/**
 * Contains build information from {@code build.gradle} retained for runtime use.
 */
public final class BuildInfo {
    @Getter private static final BuildInfo instance = new BuildInfo();
    /**
     * The semantic version of the bot, in {@code major.minor.patch-EXTRA} format.
     */
    public final @NonNull String version;
    public final @NonNull String jdaVersion;

    private BuildInfo() {
        Properties prop = IO.loadPropertiesResource("build.properties");
        version = prop.getProperty("version");
        jdaVersion = prop.getProperty("jdaVersion");
    }

    /**
     * Replaces constants in the string with their values
     * @param str A string with %constants%
     * @return The string with resolved constants from build info
     */
    public @NonNull String parsePlaceholders(@NonNull String str) {
        return str.replace("%jda_version%", jdaVersion)
                .replace("%version%", version);
    }

    /**
     * Supplies the version in a format that {@link ArgsHandler} can accept.
     */
    public static final class VersionProvider implements CommandLine.IVersionProvider {
        public String[] getVersion() {
            return new String[]{"Minecord " + getInstance().version};
        }
    }
}
