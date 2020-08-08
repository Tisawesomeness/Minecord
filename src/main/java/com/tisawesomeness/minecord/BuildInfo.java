package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.util.RequestUtils;

import lombok.Getter;

import java.util.Properties;

/**
 * Contains build information from {@code build.gradle} retained for runtime use.
 */
public final class BuildInfo {
    @Getter private static final BuildInfo instance = new BuildInfo();
    public final String version;

    private BuildInfo() {
        Properties prop = RequestUtils.loadPropertiesResource("build.properties");
        version = prop.getProperty("version");
    }
}
