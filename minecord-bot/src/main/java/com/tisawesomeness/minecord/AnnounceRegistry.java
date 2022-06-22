package com.tisawesomeness.minecord;

import com.tisawesomeness.minecord.config.branding.Announcement;
import com.tisawesomeness.minecord.config.branding.AnnouncementConfig;
import com.tisawesomeness.minecord.config.config.Config;
import com.tisawesomeness.minecord.lang.Lang;
import com.tisawesomeness.minecord.util.Mth;

import lombok.NonNull;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSetUtils;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Holds a list of possible announcements and their weights
 */
public class AnnounceRegistry {

    private final Map<Lang, MultiSet<String>> announcementsMap;
    private final boolean fallbackToDefaultLang;

    /**
     * Reads announcements from file and parses their %constants%
     */
    public AnnounceRegistry(@NonNull Config config, @NonNull BotBranding branding,
                            @NonNull AnnouncementConfig announceConf, int shardCount) {
        announcementsMap = new EnumMap<>(Lang.class);
        for (Map.Entry<Lang, List<Announcement>> entry : announceConf.getAnnouncements().entrySet()) {
            Lang lang = entry.getKey();
            List<Announcement> announcements = entry.getValue();
            announcementsMap.put(lang, buildWeightedAnnouncements(announcements, config, branding, shardCount));
        }
        fallbackToDefaultLang = announceConf.isFallbackToDefaultLang();
    }
    private static MultiSet<String> buildWeightedAnnouncements(Iterable<Announcement> announcements, Config config,
                                                               BotBranding branding, int shardCount) {
        MultiSet<String> multiSet = new HashMultiSet<>();
        for (Announcement ann : announcements) {
            String text = Placeholders.parseConstants(ann.getContent(), config, branding, shardCount);
            multiSet.add(text, ann.getWeight());
        }
        return MultiSetUtils.unmodifiableMultiSet(multiSet);
    }

    /**
     * Randomly selects an announcement based on their weights and parses their {variables}
     * @param sm The ShardManager to pull variables from
     * @return The selected announcement string
     */
    public Optional<String> roll(Lang lang, ShardManager sm) {
        MultiSet<String> announcements = announcementsMap.get(lang);
        if (announcements == null) {
            if (fallbackToDefaultLang && lang != Lang.getDefault()) {
                return roll(Lang.getDefault(), sm);
            }
            return Optional.empty();
        }
        return Optional.of(Placeholders.parseVariables(Mth.weightedRandom(announcements), sm));
    }

}
