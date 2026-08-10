package com.foodcourier.dsa.heap;

public interface HeapInterface<E> {

    int size();

    boolean isEmpty();

    void insert(E item);

    E min();

    E removeMin();

    void upheap(int index);

    void downheap(int index);
}