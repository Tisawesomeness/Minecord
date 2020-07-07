package com.tisawesomeness.minecord.listen;

import lombok.NonNull;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CountDownLatch;

public class ReadyListener extends ListenerAdapter {

    private final @NonNull CountDownLatch readyLatch;
    private final int shardCount;

    public ReadyListener(CountDownLatch readyLatch) {
        this.readyLatch = readyLatch;
        shardCount = (int) readyLatch.getCount();
    }

    @Override
    public void onReady(ReadyEvent e) {
        readyLatch.countDown();
        int readyShards = shardCount - (int) readyLatch.getCount();
        System.out.printf("%d/%d shards ready%n", readyShards, shardCount);
    }

}
