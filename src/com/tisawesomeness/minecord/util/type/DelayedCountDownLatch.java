package com.tisawesomeness.minecord.util.type;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Similar to a {@link CountDownLatch}, but the count does not have to be known in advance.
 * Threads can {@link #countDown()} before or after the starting count is set in {@link #startCountDown(int)}.
 */
public class DelayedCountDownLatch {

    private boolean started = false;
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    /**
     * Starts the countdown with the specified count.
     * @param count The count to start with
     * @throws IllegalArgumentException If the count is negative
     * @throws IllegalStateException If the countdown already started
     */
    public void startCountDown(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        lock.lock();
        try {
            if (started) {
                throw new IllegalStateException("Countdown already started");
            }
            started = true;
            this.count += count;
            if (this.count <= 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Decrements the count by 1. If the countdown has not started, then this countdown will be saved for later.
     */
    public void countDown() {
        lock.lock();
        try {
            count--;
            if (started && count <= 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Waits for the countdown to start and complete.
     * @throws InterruptedException If the thread is interrupted while waiting
     */
    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (!started || count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

}
