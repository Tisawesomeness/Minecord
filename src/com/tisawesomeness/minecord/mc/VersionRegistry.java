package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.util.RequestUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VersionRegistry {

    // List of latest minor versions: 1.7.10, 1.8.9, 1.9.4...
    private static List<Version> latestMinorVersions;

    public static void init(String path) throws IOException {
        parseVersions(path);
        if (latestMinorVersions.isEmpty()) {
            System.out.println("No versions found in versions.json");
        }
        System.out.println("Latest known version: " + latestMinorVersions.get(latestMinorVersions.size() - 1));
    }

    private static void parseVersions(String path) throws IOException {
        latestMinorVersions = new ArrayList<>();
        JSONArray versions = RequestUtils.loadJSONArray(path + "/versions.json");
        for (int i = 0; i < versions.length(); i++) {
            Version version = Version.parse(versions.getString(i));
            if (version != null) {
                latestMinorVersions.add(version);
            }
        }
    }

    public static Optional<Version> getLatestVersion() {
        if (latestMinorVersions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(latestMinorVersions.get(latestMinorVersions.size() - 1));
    }

    /**
     * Computes the previous Minecraft version of the given version.
     * Ex: 1.8.9 -> 1.8.8, 1.8.0 -> 1.7.10
     * @param version the version to compute the previous version of
     * @return the previous version, or empty if the version is the first known (1.7.0)
     */
    public static Optional<Version> getPreviousVersion(Version version) {
        if (version.getPatch() > 0) {
            return Optional.of(new Version(version.getMajor(), version.getMinor(), version.getPatch() - 1));
        } else {
            for (int i = latestMinorVersions.size() - 1; i >= 0; i--) {
                Version v = latestMinorVersions.get(i);
                if (v.compareTo(version) < 0) {
                    return Optional.of(v);
                }
            }
        }
        return Optional.empty();
    }

}
