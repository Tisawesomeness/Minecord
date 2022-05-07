package com.tisawesomeness.minecord.share;

import com.tisawesomeness.minecord.share.util.IO;

import lombok.Getter;
import lombok.NonNull;

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
}
