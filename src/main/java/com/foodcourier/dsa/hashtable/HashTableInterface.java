package com.foodcourier.dsa.hashtable;

public interface HashTableInterface<K, V> {

    int size();

    boolean isEmpty();

    V get(K key);

    void put(K key, V value);

    V remove(K key);

    boolean containsKey(K key);
}