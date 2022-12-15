package com.tisawesomeness.minecord.bootstrap;

import com.tisawesomeness.minecord.Minecord;

/**
 * Starts the bootstrapper, loading the bot normally.
 */
public final class StaticBootstrap {

    public static void main(String[] args) {
        new Bootstrap(Minecord::new).start(args);
    }

}
