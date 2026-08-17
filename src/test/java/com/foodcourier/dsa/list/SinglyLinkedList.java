package com.foodcourier.dsa.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

    private SinglyLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new SinglyLinkedList<>();
    }

    @Test
    void testAddAndSize() {
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add("First");
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());

        list.add("Second");
        list.add("Third");
        assertEquals(3, list.size());
    }

    @Test
    void testAddNullElement() {
        assertThrows(IllegalArgumentException.class, () -> list.add(null));
        assertThrows(IllegalArgumentException.class, () -> list.add(0, null));
    }

    @Test
    void testAddAtIndex() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        list.add(0, "Zero");
        assertEquals("Zero", list.get(0));
        assertEquals("First", list.get(1));
        assertEquals(4, list.size());

        list.add(2, "Middle");
        assertEquals("Middle", list.get(2));
        assertEquals("Second", list.get(3));
        assertEquals(5, list.size());

        list.add(5, "Last");
        assertEquals("Last", list.get(5));
        assertEquals("Third", list.get(4));
        assertEquals(6, list.size());
    }

    @Test
    void testAddAtIndexInvalid() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "Invalid"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, "Invalid"));
    }

    @Test
    void testGet() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        assertEquals("First", list.get(0));
        assertEquals("Second", list.get(1));
        assertEquals("Third", list.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
    }

    @Test
    void testRemoveByIndex() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        assertEquals("First", list.remove(0));
        assertEquals(2, list.size());
        assertEquals("Second", list.get(0));
        assertEquals("Third", list.get(1));

        assertEquals("Third", list.remove(1));
        assertEquals(1, list.size());
        assertEquals("Second", list.get(0));

        assertEquals("Second", list.remove(0));
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    void testRemoveByElement() {
        list.add("First");
        list.add("Second");
        list.add("Third");
        list.add("Second");

        assertTrue(list.remove("Second"));
        assertEquals(3, list.size());
        assertEquals("First", list.get(0));
        assertEquals("Third", list.get(1));
        assertEquals("Second", list.get(2));

        assertTrue(list.remove("Second"));
        assertEquals(2, list.size());
        assertEquals("First", list.get(0));
        assertEquals("Third", list.get(1));

        assertFalse(list.remove("Second"));
        assertEquals(2, list.size());
    }

    @Test
    void testRemoveNullElement() {
        list.add("First");
        assertFalse(list.remove(null));
        assertEquals(1, list.size());
    }

    @Test
    void testIndexOf() {
        list.add("First");
        list.add("Second");
        list.add("Third");
        list.add("Second");

        assertEquals(0, list.indexOf("First"));
        assertEquals(1, list.indexOf("Second"));
        assertEquals(2, list.indexOf("Third"));
        assertEquals(-1, list.indexOf("Nonexistent"));
        assertEquals(-1, list.indexOf(null));
    }

    @Test
    void testContains() {
        list.add("First");
        list.add("Second");

        assertTrue(list.contains("First"));
        assertTrue(list.contains("Second"));
        assertFalse(list.contains("Third"));
        assertFalse(list.contains(null));
    }

    @Test
    void testClear() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void testToArray() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        String[] array = list.toArray();
        assertEquals(3, array.length);
        assertEquals("First", array[0]);
        assertEquals("Second", array[1]);
        assertEquals("Third", array[2]);
    }

    @Test
    void testToArrayEmpty() {
        String[] array = list.toArray();
        assertEquals(0, array.length);
    }

    @Test
    void testEdgeCases() {
        // Add to empty list
        list.add(0, "First");
        assertEquals("First", list.get(0));

        // Remove only element
        list.remove(0);
        assertTrue(list.isEmpty());

        // Add to end when empty
        list.add("Only");
        assertEquals("Only", list.get(0));
        assertEquals(1, list.size());

        // Remove by element when not found
        assertFalse(list.remove("Nonexistent"));
    }

    @Test
    void testMaintainsTailReference() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        // Remove from middle - tail should remain "Third"
        list.remove(1);
        assertEquals("Third", list.get(1));

        // Remove from end - tail should update
        list.remove(1);
        assertEquals("First", list.get(0));
        assertEquals(1, list.size());

        // Remove last element - tail should become null
        list.remove(0);
        assertTrue(list.isEmpty());
    }
}
