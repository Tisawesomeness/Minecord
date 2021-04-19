package com.tisawesomeness.minecord.config.branding;

import com.tisawesomeness.minecord.BotBranding;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Discord;

import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Holds a list of possible announcements and their weights
 */
public class AnnounceRegistry {

    private final Map<Lang, WeightedAnnouncements> announcementsMap;
    private final boolean fallbackToDefaultLang;

    /**
     * Reads announcements from file and parses their {constants}
     */
    public AnnounceRegistry(@NonNull Config config, @NonNull BotBranding branding,
                            @NonNull AnnouncementConfig announceConf) {
        announcementsMap = new EnumMap<>(Lang.class);
        for (Map.Entry<Lang, List<Announcement>> entry : announceConf.getAnnouncements().entrySet()) {
            Lang lang = entry.getKey();
            announcementsMap.put(lang, new WeightedAnnouncements(entry, config, branding));
        }
        fallbackToDefaultLang = announceConf.isFallbackToDefaultLang();
    }

    /**
     * Randomly selects an announcement based on their weights and parses their {variables}
     * @param sm The ShardManager to pull variables from
     * @return The selected announcement string
     */
    public Optional<String> roll(Lang lang, ShardManager sm) {
        WeightedAnnouncements announcements = announcementsMap.get(lang);
        if (announcements == null) {
            if (fallbackToDefaultLang && lang != Lang.getDefault()) {
                return roll(Lang.getDefault(), sm);
            }
            return Optional.empty();
        }
        return Optional.of(Discord.parseVariables(announcements.roll(), sm));
    }

    private static class WeightedAnnouncements {
        private final List<ParsedAnnouncement> announcements;
        private final long totalWeight;

        private WeightedAnnouncements(Map.Entry<Lang, List<Announcement>> entry, Config config, BotBranding branding) {
            announcements = entry.getValue().stream()
                    .map(ann -> new ParsedAnnouncement(ann, config, branding))
                    .collect(Collectors.toList());
            // weight was already verified by the config
            totalWeight = announcements.stream()
                    .mapToLong(ann -> ann.weight)
                    .sum();
        }

        private String roll() {
            long rand = ThreadLocalRandom.current().nextLong(totalWeight);
            int i = -1;
            while (rand >= 0) {
                i++;
                rand -= announcements.get(i).weight;
            }
            return announcements.get(i).text;
        }
    }

    private static class ParsedAnnouncement {
        private final @NonNull String text;
        private final long weight;

        private ParsedAnnouncement(Announcement ann, Config config, BotBranding branding) {
            text = Discord.parseConstants(ann.getText(), config, branding);
            weight = ann.getWeight();
        }
    }

}
