package com.tisawesomeness.minecord.util.type;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A switch that can be turned on and off.
 * Threads can use {@link #waitForEnable(long, TimeUnit)} to wait for the switch to be enabled.
 */
public final class Switch {

    private boolean value = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    /**
     * Enables the switch, notifying all threads waiting in {@link #waitForEnable(long, TimeUnit)}.
     */
    public void enable() {
        lock.lock();
        try {
            value = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Disables the switch.
     */
    public void disable() {
        lock.lock();
        try {
            value = false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Waits for the switch to be enabled, or returns immediately if the switch is already enabled.
     * @param l The time to wait
     * @param timeUnit The time unit of the time to wait
     * @return True if the switch was enabled, false if the timeout was reached
     * @throws InterruptedException If the thread is interrupted while waiting
     */
    public boolean waitForEnable(long l, TimeUnit timeUnit) throws InterruptedException {
        lock.lock();
        try {
            return value || condition.await(l, timeUnit);
        } finally {
            lock.unlock();
        }
    }

}
