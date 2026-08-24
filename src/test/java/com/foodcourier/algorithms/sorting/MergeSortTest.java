package com.foodcourier.algorithms.sorting;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MergeSortTest {

    // ---------- Normal cases ----------

    @Test
    void sortsUnsortedIntegerArrayAscending() {
        Integer[] input = {5, 2, 9, 1, 5, 6};
        Integer[] expected = {1, 2, 5, 5, 6, 9};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    @Test
    void sortsStringArrayAscending() {
        String[] input = {"banana", "apple", "cherry"};
        String[] expected = {"apple", "banana", "cherry"};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    @Test
    void sortsUsingCustomComparatorDescending() {
        Integer[] input = {3, 1, 4, 1, 5, 9};
        Integer[] expected = {9, 5, 4, 3, 1, 1};
        MergeSort.sort(input, Comparator.reverseOrder());
        assertArrayEquals(expected, input);
    }

    @Test
    void sortsListInPlace() {
        List<Integer> input = new ArrayList<>(Arrays.asList(4, 2, 7, 1));
        MergeSort.sort(input, Comparator.naturalOrder());
        assertEquals(Arrays.asList(1, 2, 4, 7), input);
    }

    // ---------- Empty / trivial structures ----------

    @Test
    void handlesEmptyArrayWithoutError() {
        Integer[] input = {};
        MergeSort.sort(input);
        assertArrayEquals(new Integer[]{}, input);
    }

    @Test
    void handlesNullArrayWithoutThrowing() {
        Integer[] input = null;
        MergeSort.sort(input);
        // No exception expected — method should simply return.
    }

    @Test
    void handlesSingleElementArray() {
        Integer[] input = {42};
        MergeSort.sort(input);
        assertArrayEquals(new Integer[]{42}, input);
    }

    // ---------- Boundary cases ----------

    @Test
    void handlesArrayWithAllDuplicateElements() {
        Integer[] input = {7, 7, 7, 7};
        Integer[] expected = {7, 7, 7, 7};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    @Test
    void handlesAlreadySortedArray() {
        Integer[] input = {1, 2, 3, 4, 5};
        Integer[] expected = {1, 2, 3, 4, 5};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    @Test
    void handlesReverseSortedArray() {
        Integer[] input = {5, 4, 3, 2, 1};
        Integer[] expected = {1, 2, 3, 4, 5};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    @Test
    void handlesTwoElementArray() {
        Integer[] input = {2, 1};
        Integer[] expected = {1, 2};
        MergeSort.sort(input);
        assertArrayEquals(expected, input);
    }

    // ---------- Invalid input ----------

    @Test
    void nullComparatorOverloadDoesNotThrow() {
        Integer[] input = {3, 1, 2};
        MergeSort.sort(input, null);
        // Method should no-op rather than throw when comparator is null.
        assertArrayEquals(new Integer[]{3, 1, 2}, input);
    }

    // ---------- Stability ----------

    /**
     * Stability matters for this project: two orders can share the same
     * priority, and we want ties to keep arriving in their original
     * (e.g. FIFO intake) order after sorting for the dashboard.
     */
    @Test
    void sortIsStableForEqualKeys() {
        record Tagged(int key, String label) {
        }

        Tagged[] input = {
                new Tagged(1, "first"),
                new Tagged(2, "a"),
                new Tagged(1, "second"),
                new Tagged(2, "b"),
                new Tagged(1, "third")
        };

        MergeSort.sort(input, Comparator.comparingInt(Tagged::key));

        // All key==1 entries must stay in original relative order, likewise key==2.
        List<String> keyOneLabels = Arrays.stream(input)
                .filter(t -> t.key() == 1)
                .map(Tagged::label)
                .toList();
        List<String> keyTwoLabels = Arrays.stream(input)
                .filter(t -> t.key() == 2)
                .map(Tagged::label)
                .toList();

        assertEquals(Arrays.asList("first", "second", "third"), keyOneLabels);
        assertEquals(Arrays.asList("a", "b"), keyTwoLabels);
    }

    // ---------- Correctness against larger random input ----------

    @Test
    void matchesReferenceSortOnRandomData() {
        Random random = new Random(42);
        Integer[] input = new Integer[500];
        for (int i = 0; i < input.length; i++) {
            input[i] = random.nextInt(10_000);
        }
        Integer[] reference = input.clone();
        Arrays.sort(reference); // reference only, not used inside MergeSort itself

        MergeSort.sort(input);

        assertArrayEquals(reference, input);
        assertTrue(isSortedAscending(input));
    }

    private boolean isSortedAscending(Integer[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }
}
