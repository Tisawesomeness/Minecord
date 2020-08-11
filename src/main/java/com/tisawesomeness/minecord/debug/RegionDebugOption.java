package com.tisawesomeness.minecord.debug;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegionDebugOption implements DebugOption {

    private final @NonNull ShardManager shardManager;

    public @NonNull String getName() {
        return "region";
    }

    public @NonNull String debug() {
        List<Guild> guilds = shardManager.getGuilds();
        int total = guilds.size();
        List<Region> regions = guilds.stream()
                .map(Guild::getRegion)
                .collect(Collectors.toList());
        Multiset<Region> regionCounts = EnumMultiset.create(regions);
        return regionCounts.elementSet().stream()
                .sorted(Comparator.comparingInt(regionCounts::count))
                .map(r -> printCount(regionCounts, r, total))
                .collect(Collectors.joining("\n"));
    }

    private static @NonNull String printCount(Multiset<Region> regionCounts, Region r, int total) {
        int count = regionCounts.count(r);
        return String.format("%s | `%s` | `%.2f%%`", MarkdownUtil.bold(r.getName()), count, 100.0 * count / total);
    }

}
