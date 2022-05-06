package com.tisawesomeness.minecord.util.concurrent;

import java.util.concurrent.ExecutorService;

/**
 * Decides how a {@link ExecutorService} should shut down.
 */
public enum ShutdownBehavior {
    /**
     * Call {@link ExecutorService#shutdown()}
     */
    ORDERLY(),
    /**
     * Call {@link ExecutorService#shutdownNow()}
     */
    FORCE()
}
