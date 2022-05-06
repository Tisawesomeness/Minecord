package com.tisawesomeness.minecord.util.concurrent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper for a {@link ExecutorService} that automatically closes the service when it falls out of scope.
 * <br>The provided {@link ShutdownBehavior} decides which shutdown method to call.
 * <br>Note that if tasks are still running when the service closes, those tasks may be interrupted.
 */
@RequiredArgsConstructor
public class ACExecutorService extends AbstractExecutorService implements AutoCloseable {
    private final @NonNull ExecutorService exe;
    private final ShutdownBehavior shutdownBehavior;

    @Override
    public void shutdown() {
        exe.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exe.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exe.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exe.isTerminated();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return exe.awaitTermination(l, timeUnit);
    }

    @Override
    public void execute(Runnable runnable) {
        exe.execute(runnable);
    }

    /**
     * Closes the {@link ExecutorService} using the provided {@link ShutdownBehavior}
     */
    public void close() {
        if (isShutdown()) {
            return;
        }
        if (shutdownBehavior == ShutdownBehavior.FORCE) {
            shutdownNow();
        } else {
            shutdown();
        }
    }

}
