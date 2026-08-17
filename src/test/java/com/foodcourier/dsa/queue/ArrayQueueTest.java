package com.foodcourier.dsa.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrayQueueTest {

    private ArrayQueue<String> queue;

    @BeforeEach
    void setUp() {
        queue = new ArrayQueue<>();
    }

    @Test
    void testConstructorWithValidCapacity() {
        ArrayQueue<Integer> customQueue = new ArrayQueue<>(5);
        assertEquals(0, customQueue.size());
        assertTrue(customQueue.isEmpty());
    }

    @Test
    void testConstructorWithInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ArrayQueue<>(-5));
    }

    @Test
    void testEnqueueAndSize() {
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());

        queue.enqueue("First");
        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());

        queue.enqueue("Second");
        queue.enqueue("Third");
        assertEquals(3, queue.size());
    }

    @Test
    void testEnqueueNullElement() {
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
    }

    @Test
    void testDequeue() {
        queue.enqueue("First");
        queue.enqueue("Second");
        queue.enqueue("Third");

        assertEquals("First", queue.dequeue());
        assertEquals(2, queue.size());
        assertEquals("Second", queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals("Third", queue.dequeue());
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }

    @Test
    void testDequeueEmpty() {
        assertThrows(IllegalStateException.class, () -> queue.dequeue());
    }

    @Test
    void testPeek() {
        queue.enqueue("First");
        queue.enqueue("Second");

        assertEquals("First", queue.peek());
        assertEquals(2, queue.size());
        assertEquals("First", queue.peek());
    }

    @Test
    void testPeekEmpty() {
        assertThrows(IllegalStateException.class, () -> queue.peek());
    }

    @Test
    void testResize() {
        // Fill beyond default capacity (10)
        for (int i = 0; i < 20; i++) {
            queue.enqueue("Item " + i);
        }
        assertEquals(20, queue.size());

        // Remove some to test circular behavior
        for (int i = 0; i < 10; i++) {
            assertEquals("Item " + i, queue.dequeue());
        }
        assertEquals(10, queue.size());

        // Add more to trigger resize again if needed
        for (int i = 20; i < 30; i++) {
            queue.enqueue("Item " + i);
        }
        assertEquals(20, queue.size());
    }

    @Test
    void testCircularBehavior() {
        // Fill queue partially
        for (int i = 0; i < 5; i++) {
            queue.enqueue("Item " + i);
        }

        // Remove some from front
        for (int i = 0; i < 3; i++) {
            queue.dequeue();
        }

        // Add more to wrap around
        for (int i = 5; i < 12; i++) {
            queue.enqueue("Item " + i);
        }

        assertEquals(9, queue.size());
        assertEquals("Item 3", queue.peek());
    }

    @Test
    void testFIFOOrder() {
        queue.enqueue("First");
        queue.enqueue("Second");
        queue.enqueue("Third");

        assertEquals("First", queue.dequeue());
        assertEquals("Second", queue.dequeue());
        assertEquals("Third", queue.dequeue());
    }

    @Test
    void testMultipleOperations() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertEquals("A", queue.dequeue());
        queue.enqueue("C");
        assertEquals("B", queue.dequeue());
        queue.enqueue("D");
        assertEquals("C", queue.dequeue());
        assertEquals("D", queue.dequeue());
        assertTrue(queue.isEmpty());
    }
}
