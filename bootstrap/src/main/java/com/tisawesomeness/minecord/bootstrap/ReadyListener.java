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
     * Creates a ReadyListener that can wait for the given number of shards to ready
     * @param shardCount the number of shards
     */
    public ReadyListener(int shardCount) {
        this.shardCount = shardCount;
        readyLatch = new CountDownLatch(shardCount);
    }

    @Override
    public void onReady(@NonNull ReadyEvent e) {
        readyLatch.countDown();
        int readyShards = shardCount - (int) readyLatch.getCount();
        log.info("{}/{} shards ready", readyShards, shardCount);
    }

    /**
     * Waits for all shards to ready.
     * @throws InterruptedException if interrupted
     */
    public void await() throws InterruptedException {
        readyLatch.await();
    }

}
