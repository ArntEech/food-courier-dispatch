package com.foodcourier.dsa.list;

public interface ListInterface<T> {

    void add(T item);

    void add(int index, T item);

    T get(int index);

    T remove(int index);

    boolean isEmpty();

    int size();
}
