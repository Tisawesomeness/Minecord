package com.tisawesomeness.minecord.bootstrap;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An event manager that creates a staging event manager with {@link #queueStaging()}, adds listeners to it on
 * register or unregister, then promotes the staging manager to the active manager with {@link #promoteStaging()}
 * (swaps out active for staging).
 */
public class SwapEventManager implements IEventManager {

    // Initially, both read and write references point to the active manager
    // On queueStaging(), a new staging manager is created, and write ref is updated to point to it
    // On promoteStaging(), the read ref is updated to point to the staging manager,
    // then the staging variable is cleared. Since both read and write point to the same manager, that manager becomes
    // the active manager and this swap event manager returns to its original state.

    private @Nullable IEventManager staging;
    private final AtomicReference<IEventManager> readRef = new AtomicReference<>();
    private final AtomicReference<IEventManager> writeRef = new AtomicReference<>();

    /**
     * Creates a new swap event manager with an active event manager and no staging event manager.
     */
    public SwapEventManager() {
        IEventManager em = new InterfacedEventManager();
        readRef.set(em);
        writeRef.set(em);
    }

    @Override
    public void register(@Nonnull Object listener) {
        writeRef.get().register(listener);
    }
    @Override
    public void unregister(@Nonnull Object listener) {
        writeRef.get().unregister(listener);
    }

    @Override
    public @Nonnull List<Object> getRegisteredListeners() {
        return readRef.get().getRegisteredListeners();
    }
    @Override
    public void handle(@Nonnull GenericEvent event) {
        readRef.get().handle(event);
    }

    /**
     * Creates a new staging event manager to be promoted on the next call to {@link #promoteStaging()}.
     * Newly registered listeners will be added to the staging event manager, but the staging event manager will not
     * receive events until {@link #promoteStaging()} is called.
     * @throws IllegalStateException if a staging event manager is already queued
     */
    public void queueStaging() {
        if (staging != null) {
            throw new IllegalStateException("Cannot call queueStaging() twice without a promoteStaging()");
        }
        staging = new InterfacedEventManager();
        writeRef.set(staging);
    }

    /**
     * Undoes the effects of {@link #queueStaging()}.
     * Newly registered listeners will be added to the active event manager.
     * @throws IllegalStateException if no staging event manager is queued
     */
    public void unqueueStaging() {
        if (staging == null) {
            throw new IllegalStateException("Must call queueStaging() before each unqueueStaging()");
        }
        writeRef.set(readRef.get());
        staging.getRegisteredListeners().forEach(staging::unregister); // unregister to prevent leak
        staging = null;
    }

    /**
     * Promotes the staging event manager to the active event manager.
     * The old active event manager is discarded, and any listeners that were added to the old event manager
     * are unregistered.
     * @throws IllegalStateException if no staging event manager is queued
     */
    public void promoteStaging() {
        if (staging == null) {
            throw new IllegalStateException("Must call queueStaging() before each promoteStaging()");
        }
        IEventManager temp = readRef.getAndSet(staging);
        staging = null;
        temp.getRegisteredListeners().forEach(temp::unregister); // unregister to prevent leak
    }

}