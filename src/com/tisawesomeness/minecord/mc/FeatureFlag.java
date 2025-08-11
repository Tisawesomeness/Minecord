package com.tisawesomeness.minecord.mc;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class FeatureFlag {

    private final String id;
    @Getter private final String displayName;
    private final @Nullable Version releaseVersion;

    FeatureFlag(Version version) {
        this(version.toString(), version.toString(), version);
    }

    public Optional<Version> getReleaseVersion() {
        return Optional.ofNullable(releaseVersion);
    }
    public boolean isReleased() {
        return releaseVersion != null;
    }

}
