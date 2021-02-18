package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.IOUtils;

import lombok.Getter;
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
    public final String version;
    public final String jdaVersion;

    private BuildInfo() {
        Properties prop = IOUtils.loadPropertiesResource("build.properties");
        version = prop.getProperty("version");
        jdaVersion = prop.getProperty("jdaVersion");
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
