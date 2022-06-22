package com.tisawesomeness.minecord.service;

import com.tisawesomeness.minecord.ReactMenu;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Purges inactive reaction menus once every minute.
 */
@RequiredArgsConstructor
public class MenuService extends Service {
    public void schedule(ScheduledExecutorService exe) {
        exe.scheduleAtFixedRate(ReactMenu::purge, 10, 1, TimeUnit.MINUTES);
    }
}
