package com.foodcourier.dsa.queue;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayQueueTest {

    private ArrayQueue<Integer> queue = new ArrayQueue<>();

    @org.junit.jupiter.api.Test
    void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @org.junit.jupiter.api.Test
    void enqueueIncreasesSize() {
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(2, queue.size());
        assertFalse(queue.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void dequeueReturnsItemsInFifoOrder() {
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void peekDoesNotRemoveItem() {
        queue.enqueue(42);
        assertEquals(42, queue.peek());
        assertEquals(1, queue.size());
    }

    @org.junit.jupiter.api.Test
    void dequeueOnEmptyQueueThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.dequeue());
    }

    @org.junit.jupiter.api.Test
    void peekOnEmptyQueueThrows() {
        assertThrows(NoSuchElementException.class, () -> queue.peek());
    }

    @org.junit.jupiter.api.Test
    void queueGrowsBeyondInitialCapacity() {
        for (int i = 0; i < 100; i++) {
            queue.enqueue(i);
        }
        assertEquals(100, queue.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, queue.dequeue());
        }
    }

    @org.junit.jupiter.api.Test
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
}
