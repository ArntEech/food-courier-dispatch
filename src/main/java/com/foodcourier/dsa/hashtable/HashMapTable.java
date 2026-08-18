package com.foodcourier.dsa.hashtable;

import java.util.LinkedList;

public class HashMapTable<K, V> implements HashTableInterface<K, V> {

    private static final int DEFAULT_CAPACITY = 16;

    private final LinkedList<Entry<K, V>>[] buckets;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked")
    public HashMapTable() {
        buckets = new LinkedList[DEFAULT_CAPACITY];

        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }

        size = 0;
    }

    private int getBucketIndex(K key) {
        if (key == null) {
            return 0;
        }

        return (key.hashCode() & 0x7fffffff) % buckets.length;
    }

    private boolean keysEqual(K first, K second) {
        if (first == null) {
            return second == null;
        }

        return first.equals(second);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public V get(K key) {
        int index = getBucketIndex(key);

        for (Entry<K, V> entry : buckets[index]) {
            if (keysEqual(entry.key, key)) {
                return entry.value;
            }
        }

        return null;
    }

    @Override
    public void put(K key, V value) {
        int index = getBucketIndex(key);

        for (Entry<K, V> entry : buckets[index]) {
            if (keysEqual(entry.key, key)) {
                entry.value = value;
                return;
            }
        }

        buckets[index].add(new Entry<>(key, value));
        size++;
    }

    @Override
    public V remove(K key) {
        int index = getBucketIndex(key);

        for (int i = 0; i < buckets[index].size(); i++) {
            Entry<K, V> entry = buckets[index].get(i);

            if (keysEqual(entry.key, key)) {
                buckets[index].remove(i);
                size--;
                return entry.value;
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int index = getBucketIndex(key);

        for (Entry<K, V> entry : buckets[index]) {
            if (keysEqual(entry.key, key)) {
                return true;
            }
        }

        return false;
    }
}