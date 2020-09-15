package com.tisawesomeness.minecord.service;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Code that runs periodically in another thread, which can be started and stopped.
 * If {@link #shouldRun()} is false, this service safely does nothing.
 */
public abstract class Service {
    private @Nullable ScheduledExecutorService exe;

    /**
     * If {@link #shouldRun()} is true, starts this service and allocates the Executor.
     */
    public final void start() {
        if (shouldRun()) {
            exe = Executors.newSingleThreadScheduledExecutor();
            schedule(exe);
        }
    }

    /**
     * Shuts down this service.
     * <br>If {@link #shouldRun()} is true, this closes the Executor for this service.
     */
    public final void shutdown() {
        if (exe != null) {
            exe.shutdown(); // shut up exe
        }
    }

    /**
     * Called from {@link #start()} to determine whether to schedule this service.
     * @return Whether this service should be scheduled
     */
    public boolean shouldRun() {
        return true;
    }

    /**
     * Schedules all tasks for this service.
     * <br>Called from {@link #start()} only if {@link #shouldRun()} is true.
     * @param exe The Executor for this service
     */
    public abstract void schedule(ScheduledExecutorService exe);
}
