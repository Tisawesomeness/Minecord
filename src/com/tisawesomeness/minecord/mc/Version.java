package com.tisawesomeness.minecord.mc;

import lombok.Value;

import javax.annotation.Nullable;
import java.util.Comparator;

@Value
public class Version implements Comparable<Version> {

    public static final Comparator<Version> NULLS_FIRST_COMPARATOR = Comparator.nullsFirst(Comparator.naturalOrder());

    int major;
    int minor;
    int patch;

    public Version(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("All parts of the version must be nonnegative");
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static @Nullable Version parse(@Nullable String version) {
        if (version == null) {
            return null;
        }
        String[] parts = version.split("\\.");
        if (parts.length != 2 && parts.length != 3) {
            return null;
        }
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
            return new Version(major, minor, patch);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public int compareTo(Version o) {
        if (major != o.major) {
            return Integer.compare(major, o.major);
        }
        if (minor != o.minor) {
            return Integer.compare(minor, o.minor);
        }
        return Integer.compare(patch, o.patch);
    }

    @Override
    public String toString() {
        if (patch == 0) {
            return major + "." + minor;
        }
        return major + "." + minor + "." + patch;
    }

}
