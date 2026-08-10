package com.foodcourier.dsa.list;

public interface ListInterface<E> {

    int size();

    boolean isEmpty();

    E get(int index);

    void addFirst(E item);

    void addLast(E item);

    E removeFirst();

    E removeLast();
}