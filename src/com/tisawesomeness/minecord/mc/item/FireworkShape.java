package com.tisawesomeness.minecord.mc.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FireworkShape {
    SMALL_BALL("small_ball"),
    LARGE_BALL("large_ball"),
    STAR("star"),
    CREEPER("creeper"),
    BURST("burst");

    @Getter private final String id;
}
