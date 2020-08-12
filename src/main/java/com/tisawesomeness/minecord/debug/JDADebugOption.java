package com.tisawesomeness.minecord.debug;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Debugs JDA shard gateway and rest ping times.
 */
@RequiredArgsConstructor
public class JDADebugOption implements DebugOption {

    private final @NonNull ShardManager shardManager;
    public @NonNull String getName() {
        return "JDA";
    }

    public @NonNull String debug(@NonNull String extra) {
        List<JDA> shards = shardManager.getShards(); // Not guarenteed to be sorted by shard id
        // Submitting all ping requests all at once
        // Instead of waiting for one to finish to submit the nextp
        List<CompletableFuture<Long>> shardPings = shards.stream()
                .map(jda -> jda.getRestPing().submit())
                .collect(Collectors.toList());

        Map<Integer, String> shardStrings = new HashMap<>();
        for (int i = 0; i < shardPings.size(); i++) {
            shardStrings.put(i, getShardLine(shardPings.get(i), shards.get(i)));
        }
        return shardStrings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())) // Ascending order by shard id
                .map(Map.Entry::getValue)
                .collect(Collectors.joining("\n"));
    }

    private static String getShardLine(CompletableFuture<Long> shardPing, JDA shard) {
        int id = shard.getShardInfo().getShardId() + 1;
        long gatewayPing = shard.getGatewayPing();
        String str = String.format("**Shard %s:** Gateway Ping `%sms`", id, gatewayPing);
        try {
            long restPing = shardPing.get();
            return str + String.format(" | Rest Ping `%sms`", restPing);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return str + " | Error getting rest ping.";
    }

}
