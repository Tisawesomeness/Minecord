package com.tisawesomeness.minecord.common;

/**
 * An interface that lets a bot access bootstrap methods.
 */
public interface BootstrapHook {
    int reload();
    void shutdown();
}
