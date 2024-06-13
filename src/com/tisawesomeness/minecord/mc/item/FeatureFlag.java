package com.tisawesomeness.minecord.mc.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum FeatureFlag {
    BUNDLE("bundle", "Bundle", null),
    UPDATE_1_20("1.20"),
    UPDATE_1_21("1.21");

    private final String id;
    private final String displayName;
    private final @Nullable String releaseVersion;

    FeatureFlag(String version) {
        this(version, version, version);
    }

    public Optional<String> getReleaseVersion() {
        return Optional.ofNullable(releaseVersion);
    }
    public boolean isReleased() {
        return releaseVersion != null;
    }

    public static Optional<FeatureFlag> from(String str) {
        return Arrays.stream(values())
                .filter(f -> f.getId().equals(str))
                .findFirst();
    }
}
