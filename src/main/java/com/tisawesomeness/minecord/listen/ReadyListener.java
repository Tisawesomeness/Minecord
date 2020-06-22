package com.tisawesomeness.minecord.listen;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {

    private final @NonNull CountDownLatch readyLatch;

    @Override
    public void onReady(ReadyEvent e) {
        readyLatch.countDown();
        System.out.println(readyLatch.getCount() + " shards left.");
    }

}
