package com.foodcourier.dsa.disjointset;

import java.util.*;

public class DisjointSet<E> implements DisjointSetInterface<E> {

    // Stores each element and its parent
    private Map<E, E> parent;

    // Stores the rank of each tree
    private Map<E, Integer> rank;

    // Constructor
    public DisjointSet() {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    // Create a new set
    @Override
    public void makeSet(E item) {
        if (!parent.containsKey(item)) {
            parent.put(item, item);
            rank.put(item, 0);
        }
    }

    // Find the root of an element
    @Override
    public E find(E item) {
        if (!parent.containsKey(item)) {
            return null;
        }

        // Apply path compression
        if (!item.equals(parent.get(item))) {
            E root = find(parent.get(item));
            parent.put(item, root);
            return root;
        }

        return item;
    }

    // Join two sets
    @Override
    public void union(E first, E second) {
        E rootFirst = find(first);
        E rootSecond = find(second);

        if (rootFirst == null || rootSecond == null ||
                rootFirst.equals(rootSecond)) {
            return;
        }

        // Use union by rank
        int rankFirst = rank.get(rootFirst);
        int rankSecond = rank.get(rootSecond);

        if (rankFirst < rankSecond) {
            parent.put(rootFirst, rootSecond);
        } else if (rankFirst > rankSecond) {
            parent.put(rootSecond, rootFirst);
        } else {
            parent.put(rootSecond, rootFirst);
            rank.put(rootFirst, rankFirst + 1);
        }
    }

    // Check if two elements are connected
    @Override
    public boolean connected(E first, E second) {
        E rootFirst = find(first);
        E rootSecond = find(second);

        if (rootFirst == null || rootSecond == null) {
            return false;
        }

        return rootFirst.equals(rootSecond);
    }

    // Get all elements
    public Set<E> getElements() {
        return parent.keySet();
    }

    // Get the number of sets
    public int getSetCount() {
        int count = 0;

        for (E item : parent.keySet()) {
            if (item.equals(parent.get(item))) {
                count++;
            }
        }

        return count;
    }

    // Print the set structure
    public void printStructure() {
        System.out.println("=== Disjoint Set Structure ===");

        for (E item : parent.keySet()) {
            E root = find(item);
            System.out.println(
                    item + " -> " + root +
                    " (rank: " + rank.get(item) + ")"
            );
        }

        System.out.println("Total elements: " + parent.size());
        System.out.println("Number of sets: " + getSetCount());
        System.out.println("==============================");
    }
}