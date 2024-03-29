package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.common.util.Verification;
import com.tisawesomeness.minecord.lang.Lang;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AnnouncementConfig {
    @JsonProperty("enabled")
    boolean enabled;
    @JsonProperty("fallbackToDefaultLang")
    boolean fallbackToDefaultLang;
    @JsonProperty("announcements")
    Map<Lang, List<Announcement>> announcements;

    public Verification verify() {
        if (!enabled) {
            return Verification.valid();
        }
        return Verification.combineAll(
                verifyAnnouncements(),
                verifyTotalWeights()
        );
    }
    private Verification verifyAnnouncements() {
        return announcements.values().stream()
                .flatMap(List::stream)
                .map(Announcement::verify)
                .reduce(Verification::combine)
                .orElse(Verification.valid());
    }
    private Verification verifyTotalWeights() {
        return announcements.values().stream()
                .map(AnnouncementConfig::verifyTotalWeight)
                .reduce(Verification::combine)
                .orElse(Verification.valid());
    }
    private static Verification verifyTotalWeight(Iterable<Announcement> announcements) {
        int weight = 0;
        for (Announcement announcement : announcements) {
            weight += announcement.getWeight();
            if (weight < 0) {
                return Verification.invalid("The total weight was so high it caused an integer overflow.");
            }
        }
        return Verification.verify(weight != 0, "The total weight cannot be 0.");
    }

}
