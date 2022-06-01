package com.tisawesomeness.minecord.bootstrap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SwapEventManagerTest {

    @Test
    @DisplayName("Queue then promote is valid")
    public void testQueueThenPromote() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        assertThatNoException().isThrownBy(sem::promoteStaging);
    }
    @Test
    @DisplayName("Queue then undo is valid")
    public void testQueueThenUndo() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        assertThatNoException().isThrownBy(sem::unqueueStaging);
    }
    @Test
    @DisplayName("Queue then undo then queue then promote is valid")
    public void testAll() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        sem.unqueueStaging();
        sem.queueStaging();
        assertThatNoException().isThrownBy(sem::promoteStaging);
    }

    @Test
    @DisplayName("Cannot promote without queueing")
    public void testPromoteFirst() {
        SwapEventManager sem = new SwapEventManager();
        assertThatThrownBy(sem::promoteStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot undo without queueing")
    public void testUndoFirst() {
        SwapEventManager sem = new SwapEventManager();
        assertThatThrownBy(sem::unqueueStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot queue again without promoting")
    public void testDoubleQueue() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        assertThatThrownBy(sem::queueStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot promote again without queueing")
    public void testDoublePromote() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        sem.promoteStaging();
        assertThatThrownBy(sem::promoteStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot undo again without queueing")
    public void testDoubleUndo() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        sem.unqueueStaging();
        assertThatThrownBy(sem::unqueueStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot promote then undo")
    public void testPromoteUndo() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        sem.promoteStaging();
        assertThatThrownBy(sem::unqueueStaging).isInstanceOf(IllegalStateException.class);
    }
    @Test
    @DisplayName("Cannot undo then promote")
    public void testUndoPromote() {
        SwapEventManager sem = new SwapEventManager();
        sem.queueStaging();
        sem.unqueueStaging();
        assertThatThrownBy(sem::promoteStaging).isInstanceOf(IllegalStateException.class);
    }

}
