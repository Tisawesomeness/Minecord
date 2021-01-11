package com.tisawesomeness.minecord.util.concurrent;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A utility class for adding callbacks when a {@link CompletableFuture} completes.
 */
@Slf4j
public final class FutureCallback {
    private FutureCallback() {}

    /**
     * Creates a new callback builder. No changes will be applied until {@link Builder#build()} is called.
     * @param future The future to have callbacks added to
     * @param <T> The type of the future
     * @return A new builder
     */
    public static <T> Builder<T> builder(@NonNull CompletableFuture<T> future) {
        return new Builder<>(future);
    }

    /**
     * Builds success and error callbacks.
     * @param <T> The type of the provided future
     * @implNote This class does not violate the future's thread-safety,
     * but this class itself is <b>not</b> thread-safe.
     */
    public static class Builder<T> {
        private final @NonNull CompletableFuture<T> future;
        private @Nullable Consumer<? super T> successFn;
        private @Nullable Consumer<? super Throwable> errorFn;
        private @Nullable Consumer<? super Throwable> uncaughtFn;

        private Builder(@NonNull CompletableFuture<T> future) {
            this.future = future;
        }

        /**
         * Sets the success callback.
         * @param successFn The success function to run when the future succeeds
         * @return This builder
         */
        public Builder<T> onSuccess(@Nullable Consumer<? super T> successFn) {
            this.successFn = successFn;
            return this;
        }
        /**
         * Sets the failure callback.
         * @param errorFn The error function to run when the future completes exceptionally
         * @return This builder
         */
        public Builder<T> onFailure(@Nullable Consumer<? super Throwable> errorFn) {
            this.errorFn = errorFn;
            return this;
        }
        /**
         * Sets the uncaught callback. Defaults to simply logging the exception.
         * @param uncaughtFn The function to run when the success or error function throw an uncaught exception
         * @return This builder
         */
        public Builder<T> onUncaught(@Nullable Consumer<? super Throwable> uncaughtFn) {
            this.uncaughtFn = uncaughtFn;
            return this;
        }

        /**
         * Adds this builder's callbacks to the provided future.
         * @return The CompletableFuture with added callbacks
         */
        public CompletableFuture<T> build() {
            return future.whenComplete(this::onComplete);
        }
        private void onComplete(T val, Throwable err) {
            if (err == null) {
                if (successFn != null) {
                    trySuccess(val);
                }
            } else if (errorFn != null) {
                tryError(err);
            }
        }
        private void trySuccess(T val) {
            try {
                successFn.accept(val);
            } catch (Throwable ex) {
                if (uncaughtFn != null) {
                    uncaughtFn.accept(ex);
                } else {
                    log.error("Uncaught exception in success callback", ex);
                }
            }
        }
        private void tryError(Throwable err) {
            try {
                errorFn.accept(err);
            } catch (Throwable ex) {
                if (uncaughtFn != null) {
                    uncaughtFn.accept(ex);
                } else {
                    log.error("Uncaught exception in error callback", ex);
                }
            }
        }
    }

}
