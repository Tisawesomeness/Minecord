package com.tisawesomeness.minecord.listen;

import com.tisawesomeness.minecord.Bot;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {

    private final @NonNull Bot bot;

    @Override
    public void onReady(ReadyEvent e) {
        bot.addReadyShard();
    }

}
