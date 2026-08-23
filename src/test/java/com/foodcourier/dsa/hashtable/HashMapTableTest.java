package com.foodcourier.dsa.hashtable;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class HashMapTableTest {

    @Test
    void newTableShouldBeEmpty() {
        HashMapTable<Integer, String> table = new HashMapTable<>();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void putAndGetShouldWork() {
        HashMapTable<Integer, String> table = new HashMapTable<>();

        table.put(1, "Courier A");
        table.put(2, "Courier B");

        assertEquals("Courier A", table.get(1));
        assertEquals("Courier B", table.get(2));
        assertEquals(2, table.size());
    }

    @Test
    void containsKeyShouldWork() {
        HashMapTable<Integer, String> table = new HashMapTable<>();

        table.put(10, "Courier");

        assertTrue(table.containsKey(10));
        assertFalse(table.containsKey(99));
    }

    @Test
    void removeShouldWork() {
        HashMapTable<Integer, String> table = new HashMapTable<>();

        table.put(5, "Courier A");

        String removed = table.remove(5);

        assertEquals("Courier A", removed);
        assertFalse(table.containsKey(5));
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
    }
}
