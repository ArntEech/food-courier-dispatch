package com.foodcourier.dsa.queue;

import dsa.queue.ArrayQueue;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayQueueTest {

    private ArrayQueue<Integer> queue = new ArrayQueue<>();

    @Test
    void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void enqueueIncreasesSize() {
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(2, queue.size());
        assertFalse(queue.isEmpty());
    }

    @Test
    void dequeueReturnsItemsInFifoOrder() {
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemoveItem() {
        queue.enqueue(42);
        assertEquals(42, queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    void dequeueOnEmptyQueueThrows() {
        assertEquals(NoSuchElementException.class, assertThrows(NoSuchElementException.class, () -> queue.dequeue()).getClass());
    }

    @Test
    void peekOnEmptyQueueThrows() {
        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () -> queue.peek());
    }

    @Test
    void queueGrowsBeyondInitialCapacity() {
        for (int i = 0; i < 100; i++) {
            queue.enqueue(i);
        }
        assertEquals(100, queue.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, queue.dequeue());
        }
    }

    @Test
    void wrapAroundBehavesCorrectly() {
        ArrayQueue<Integer> small = new ArrayQueue<>(4);
        small.enqueue(1);
        small.enqueue(2);
        small.dequeue();
        small.enqueue(3);
        small.enqueue(4);
        small.enqueue(5);

        assertEquals(2, small.dequeue());
        assertEquals(3, small.dequeue());
        assertEquals(4, small.dequeue());
        assertEquals(5, small.dequeue());
        assertTrue(small.isEmpty());
    }

    private void assertEquals(int i, Integer dequeue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void assertTrue(boolean empty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void assertFalse(boolean empty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
