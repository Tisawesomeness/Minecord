package com.tisawesomeness.minecord.mc;

import com.tisawesomeness.minecord.util.RequestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class FeatureFlagRegistry {

    private static List<FeatureFlag> flags;

    public static void init(String path) throws IOException {
        parseFlags(RequestUtils.loadJSONArray(path + "/flags.json"));
        System.out.println("Loaded " + flags.size() + " feature flags");
    }
    private static void parseFlags(JSONArray flagsArr) {
        flags = new ArrayList<>();
        for (int i = 0; i < flagsArr.length(); i++) {
            JSONObject flagObj = flagsArr.getJSONObject(i);
            flags.add(parseFlag(flagObj));
        }
    }
    private static FeatureFlag parseFlag(JSONObject flagObj) {
        String id = flagObj.getString("id");
        if (id.equals("vanilla")) {
            return null;
        }
        Version version = Version.parse(id);
        if (version != null) {
            return new FeatureFlag(version);
        } else {
            String name = flagObj.optString("name", id);
            Version release = Version.parse(flagObj.optString("release"));
            return new FeatureFlag(id, name, release);
        }
    }

    public static final Comparator<FeatureFlag> RELEASE_ORDER_COMPARATOR = Comparator.comparingInt(f -> flags.indexOf(f));

    /**
     * @return all feature flags in release order, with `null` separating released and unreleased feature flags
     */
    public static List<FeatureFlag> getFlags() {
        return Collections.unmodifiableList(flags);
    }

    public static Optional<FeatureFlag> get(String id) {
        return flags.stream()
                .filter(Objects::nonNull)
                .filter(f -> f.getId().equals(id))
                .findFirst();
    }

}
