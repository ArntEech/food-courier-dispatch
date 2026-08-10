package com.foodcourier.dsa.queue;

public interface QueueInterface<E> {

    int size();

    boolean isEmpty();

    void enqueue(E item);

    E dequeue();

    E first();
}