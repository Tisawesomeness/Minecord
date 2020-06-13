package com.tisawesomeness.minecord;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {

    @NonNull private final Bot bot;

    @Override
    public void onReady(ReadyEvent e) {
        bot.addReadyShard();
    }

}
