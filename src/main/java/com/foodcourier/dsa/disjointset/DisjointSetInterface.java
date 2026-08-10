package com.foodcourier.dsa.disjointset;

public interface DisjointSetInterface<E> {

    void makeSet(E item);

    E find(E item);

    void union(E first, E second);

    boolean connected(E first, E second);
}