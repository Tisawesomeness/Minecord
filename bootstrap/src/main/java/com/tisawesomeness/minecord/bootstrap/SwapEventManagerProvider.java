package com.tisawesomeness.minecord.bootstrap;

import net.dv8tion.jda.api.hooks.IEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Provides swap event managers for each shard, and keeps a list of them so the listeners can be swapped.
 */
public class SwapEventManagerProvider implements IntFunction<IEventManager> {

    private final List<SwapEventManager> ems;

    public SwapEventManagerProvider(int shardCount) {
        ems = new ArrayList<>(shardCount);
    }

    /**
     * Maps the shard ID to the corresponding {@link SwapEventManager}.
     * @param i The shard ID.
     * @return The corresponding {@link SwapEventManager}.
     */
    public IEventManager apply(int i) {
        SwapEventManager em = new SwapEventManager();
        ems.add(em);
        return em;
    }

    /**
     * Runs {@link SwapEventManager#queueStaging()} for all shards.
     */
    public void queueStaging() {
        ems.forEach(SwapEventManager::queueStaging);
    }
    /**
     * Runs {@link SwapEventManager#promoteStaging()} for all shards.
     */
    public void promoteStaging() {
        ems.forEach(SwapEventManager::promoteStaging);
    }

}
