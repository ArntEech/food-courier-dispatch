package com.foodcourier.dsa.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ArrayQueue<T> implements QueueInterface<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] elements;
    private int front;
    private int size;

    public ArrayQueue() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.elements = new Object[initialCapacity];
        this.front = 0;
        this.size = 0;
    }

    public void enqueue(T item) {
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        int rear = (front + size) % elements.length;
        elements[rear] = item;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue from an empty queue");
        }
        T item = (T) elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot peek an empty queue");
        }
        return (T) elements[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public List<T> toList() {
        List<T> snapshot = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T item = (T) elements[(front + i) % elements.length];
            snapshot.add(item);
        }
        return snapshot;
    }

    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
    }

    private void resize(int newCapacity) {
        Object[] newElements = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(front + i) % elements.length];
        }
        elements = newElements;
        front = 0;
    }
}
