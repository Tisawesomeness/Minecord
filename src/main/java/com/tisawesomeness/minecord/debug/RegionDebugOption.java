package com.tisawesomeness.minecord.debug;

import com.tisawesomeness.minecord.util.type.EnumMultiSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.apache.commons.collections4.MultiSet;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegionDebugOption implements DebugOption {

    private final @NonNull ShardManager shardManager;

    public @NonNull String getName() {
        return "region";
    }

    public @NonNull String debug(@NonNull String extra) {
        List<Guild> guilds = shardManager.getGuilds();
        int total = guilds.size();
        if (total == 0) {
            return "no guilds";
        }
        List<Region> regions = guilds.stream()
                .map(Guild::getRegion)
                .collect(Collectors.toList());
        MultiSet<Region> regionCounts = new EnumMultiSet<>(regions);
        return regionCounts.entrySet().stream()
                .sorted(Comparator.comparingInt(MultiSet.Entry::getCount))
                .map(en -> printCount(en, total))
                .collect(Collectors.joining("\n"));
    }

    private static @NonNull String printCount(MultiSet.Entry<Region> regionEntry, int total) {
        Region region = regionEntry.getElement();
        int count = regionEntry.getCount();
        double rate = total == 0 ? 100.0 : 100.0 * count / total;
        return String.format("%s | `%s` | `%.2f%%`", MarkdownUtil.bold(region.getName()), count, rate);
    }

}
