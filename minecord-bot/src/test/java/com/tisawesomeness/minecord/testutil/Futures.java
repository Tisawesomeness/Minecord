package com.tisawesomeness.minecord.testutil;

import java.time.Duration;
import java.util.concurrent.*;

public class Futures {

    /**
     * Creates a new failed future.
     * @param ex the exception to fail with
     * @param <T> the type of the future
     * @return a failed future
     */
    public static <T> CompletableFuture<T> failedFuture(Throwable ex) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(ex);
        return future;
    }

    /**
     * Joins a future, throwing {@link CompletionException} if the timeout fails.
     * @param future the future to join
     * @param timeout the timeout
     * @param <T> the type of the future
     * @return the result of the future
     */
    public static <T> T joinTimeout(CompletableFuture<T> future, Duration timeout) {
        return joinTimeout(future, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
    /**
     * Joins a future, throwing {@link CompletionException} if the timeout fails.
     * @param future the future to join
     * @param l maximum time to wait
     * @param unit the time unit of the timeout
     * @param <T> the type of the future
     * @return the result of the future
     */
    public static <T> T joinTimeout(CompletableFuture<T> future, long l, TimeUnit unit) {
        try {
            return future.get(l, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new CompletionException(e);
        }
    }

}
