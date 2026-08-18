package com.foodcourier.algorithms.searching;

import java.util.Comparator;

/**
 * Binary Search — Alpha 3 (Courier Assignment).
 *
 * <p>Used to find a courier in an array that has already been sorted by some
 * attribute (e.g. distance from restaurant, rating, or zone — exact key is
 * still being finalised with Maa Afia's BST design, so this class is generic
 * over any {@code Comparator<T>} rather than hard-coded to one field).
 *
 * <p><b>Why generic + Comparator, not hard-coded to Courier:</b> the same
 * algorithm is reused elsewhere in the project (e.g. searching sorted Orders),
 * and it lets us swap the sort key (rating vs distance vs zone) without
 * touching this class once the team decides.
 *
 * <p><b>Precondition (critical — this is also our required counterexample):</b>
 * binary search is only correct on data that is already sorted according to
 * the same comparator used to search it. Calling this on unsorted input does
 * not throw by default in most textbook implementations — it just silently
 * returns wrong answers. We deliberately check the precondition here and
 * fail loudly instead, both because it is safer for the dispatch system and
 * because "binary search on unsorted input" is one of Alpha 3's required
 * invalid-precondition test cases / counterexamples (project Table 8).
 *
 * <p><b>Complexity:</b> O(log n) comparisons in the worst case, O(1) extra
 * space (iterative, not recursive — avoids stack growth for large courier
 * pools, e.g. the 20,000-key stress test in Table 7).
 */
public final class BinarySearch {

    // Not meant to be instantiated — every method here is a stateless,
    // reusable static utility (matches how LinearSearch is used elsewhere).
    private BinarySearch() {
    }

    /**
     * Searches {@code sortedArray} for {@code target} using {@code comparator}
     * to define ordering and equality.
     *
     * @param sortedArray array already sorted ascending according to comparator
     * @param target      the value being searched for
     * @param comparator  defines the sort order / equality used for comparisons
     * @param <T>         element type (e.g. Courier, Integer, Order)
     * @return index of a matching element in {@code sortedArray}, or -1 if not found
     * @throws IllegalArgumentException if {@code sortedArray} is null, if
     *         {@code comparator} is null, or if {@code sortedArray} is not
     *         actually sorted according to {@code comparator} (the required
     *         invalid-precondition case)
     */
    public static <T> int search(T[] sortedArray, T target, Comparator<T> comparator) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        if (!isSorted(sortedArray, comparator)) {
            // This is the deliberate "invalid precondition" failure mode —
            // do not silently search unsorted data.
            throw new IllegalArgumentException(
                    "Precondition violated: sortedArray is not sorted according to the given comparator. "
                    + "Binary search requires sorted input - use LinearSearch for unsorted data."
            );
        }

        int low = 0;
        int high = sortedArray.length - 1;

        // Loop invariant: if target is present in sortedArray, its index
        // lies within [low, high] at the top of every iteration.
        while (low <= high) {
            int mid = low + (high - low) / 2; // avoids overflow vs (low + high) / 2

            int cmp = comparator.compare(sortedArray[mid], target);

            if (cmp == 0) {
                return mid; // found
            } else if (cmp < 0) {
                low = mid + 1;  // target must be to the right, if present
            } else {
                high = mid - 1; // target must be to the left, if present
            }
        }

        return -1; // not found - low > high, invariant guarantees target is absent
    }

    /**
     * Convenience overload for elements that are naturally {@link Comparable}
     * (e.g. Integer distances), using their natural ordering.
     */
    public static <T extends Comparable<T>> int search(T[] sortedArray, T target) {
        return search(sortedArray, target, Comparator.naturalOrder());
    }

    /**
     * Checks whether {@code array} is sorted ascending according to
     * {@code comparator}. Public (not just a private helper) because both
     * the precondition check above and the unit tests need it.
     *
     * <p>O(n) check - cheap relative to the correctness risk of skipping it.
     */
    public static <T> boolean isSorted(T[] array, Comparator<T> comparator) {
        for (int i = 1; i < array.length; i++) {
            if (comparator.compare(array[i - 1], array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
