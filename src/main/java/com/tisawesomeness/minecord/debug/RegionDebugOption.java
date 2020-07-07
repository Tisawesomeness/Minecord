package com.tisawesomeness.minecord.debug;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegionDebugOption implements DebugOption {

    private final @NonNull ShardManager shardManager;

    public @NonNull String getName() {
        return "region";
    }

    public @NonNull String debug() {
        List<Region> regions = shardManager.getGuilds().stream()
                .map(Guild::getRegion)
                .collect(Collectors.toList());
        Multiset<Region> regionCounts = EnumMultiset.create(regions);
        return Arrays.stream(Region.values())
                .filter(regionCounts::contains)
                .sorted()
                .map(r -> MarkdownUtil.bold(r.getName()) + " : " + regionCounts.count(r))
                .collect(Collectors.joining("\n"));
    }

}
