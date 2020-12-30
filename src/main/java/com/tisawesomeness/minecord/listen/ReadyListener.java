package com.tisawesomeness.minecord.listen;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReadyListener extends ListenerAdapter {

    private final @NonNull CountDownLatch readyLatch;
    private final int shardCount;

    public ReadyListener(CountDownLatch readyLatch) {
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
