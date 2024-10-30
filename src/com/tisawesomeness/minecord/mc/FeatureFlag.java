package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.util.ArrayUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum FeatureFlag {
    BUNDLE("bundle", "Bundle", "1.21.2"),
    UPDATE_1_20("1.20"),
    UPDATE_1_21("1.21"),
    WINTER_DROP("winter_drop", "Winter Drop", null);

    public static final FeatureFlag[] RELEASE_ORDER = new FeatureFlag[]{
            UPDATE_1_20, UPDATE_1_21, BUNDLE, null, WINTER_DROP
    };
    public static final Comparator<FeatureFlag> RELEASE_ORDER_COMPARATOR = (f1, f2) -> {
        int i1 = ArrayUtils.indexOf(RELEASE_ORDER, f1);
        int i2 = ArrayUtils.indexOf(RELEASE_ORDER, f2);
        return Integer.compare(i1, i2);
    };

    private final String id;
    @Getter private final String displayName;
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
