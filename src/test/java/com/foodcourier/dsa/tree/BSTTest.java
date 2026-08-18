package com.foodcourier.dsa.tree;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BSTTest {

    @Test
    void newTreeShouldBeEmpty() {
        BST<Integer> tree = new BST<>();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }

    @Test
    void insertAndSearchShouldWork() {
        BST<Integer> tree = new BST<>();

        tree.insert(10);
        tree.insert(5);
        tree.insert(15);

        assertEquals(10, tree.search(10));
        assertEquals(5, tree.search(5));
        assertEquals(15, tree.search(15));
        assertEquals(3, tree.size());
    }

    @Test
    void containsShouldWork() {
        BST<Integer> tree = new BST<>();

        tree.insert(20);
        tree.insert(10);

        assertTrue(tree.contains(20));
        assertTrue(tree.contains(10));
        assertFalse(tree.contains(99));
    }

    @Test
    void removeShouldWork() {
        BST<Integer> tree = new BST<>();

        tree.insert(10);
        tree.insert(5);
        tree.insert(15);

        tree.remove(5);

        assertFalse(tree.contains(5));
        assertEquals(2, tree.size());
    }
}
