package com.foodcourier.dsa.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

public class SinglyLinkedListTest {

    private SinglyLinkedList<String> list;

    public SinglyLinkedListTest() {
        list = new SinglyLinkedList<>();
    }

    @Test
    public void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void addAppendsToEnd() {
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void addAtIndexInsertsCorrectly() {
        list.add("a");
        list.add("c");
        list.add(1, "b");

        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
        assertEquals(3, list.size());
    }

    @Test
    public void addAtHead() {
        list.add("b");
        list.add(0, "a");

        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }

    @Test
    public void removeByIndexShrinksListAndReturnsItem() {
        list.add("a");
        list.add("b");
        list.add("c");

        String removed = list.remove(1);

        assertEquals("b", removed);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1));
    }

    @Test
    public void removeHeadUpdatesHeadCorrectly() {
        list.add("a");
        list.add("b");
        list.remove(0);

        assertEquals("b", list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    public void removeTailUpdatesTailCorrectly() {
        list.add("a");
        list.add("b");
        list.remove(1);
        list.add("c");

        assertEquals("a", list.get(0));
        assertEquals("c", list.get(1));
    }

    @Test
    public void getOutOfBoundsThrows() {
        list.add("a");

        try {
            list.get(5);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }
        try {
            list.get(-1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }
    }

    @Test
    public void removeOutOfBoundsThrows() {
        try {
            list.remove(0);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }
    }

    @Test
    public void iteratorTraversesInOrder() {
        list.add("a");
        list.add("b");
        list.add("c");

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        assertEquals("abc", sb.toString());
    }

}
