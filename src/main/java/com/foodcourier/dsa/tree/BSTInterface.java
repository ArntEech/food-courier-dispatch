package com.foodcourier.dsa.tree;

public interface BSTInterface<E> {

    int size();

    boolean isEmpty();

    void insert(E item);

    boolean contains(E item);

    E search(E item);

    void remove(E item);
}