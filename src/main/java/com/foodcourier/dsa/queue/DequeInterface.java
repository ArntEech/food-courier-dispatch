package com.foodcourier.dsa.queue;

public interface DequeInterface<E> {

    int size();

    boolean isEmpty();

    void addFirst(E item);

    void addLast(E item);

    E removeFirst();

    E removeLast();

    E first();

    E last();
}