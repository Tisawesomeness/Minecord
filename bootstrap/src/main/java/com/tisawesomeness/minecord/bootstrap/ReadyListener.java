package com.tisawesomeness.minecord.bootstrap;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * Listener that keeps track of how many shards are ready.
 */
@Slf4j
public class ReadyListener extends ListenerAdapter {

    private final @NonNull CountDownLatch readyLatch;
    private final int shardCount;

    /**
     * Creates a ReadyListener that decrements the given latch when a shard is ready.
     * @param readyLatch a latch initialized with the number of shards
     */
    public ReadyListener(@NonNull CountDownLatch readyLatch) {
        this.readyLatch = readyLatch;
        shardCount = (int) readyLatch.getCount();
    }

    @Override
    public void onReady(@NonNull ReadyEvent e) {
        readyLatch.countDown();
        int readyShards = shardCount - (int) readyLatch.getCount();
        log.info(String.format("%d/%d shards ready", readyShards, shardCount));
    }

}
