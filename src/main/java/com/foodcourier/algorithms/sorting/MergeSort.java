package com.foodcourier.algorithms.sorting;

import java.util.Comparator;
import java.util.List;

/**
 * Custom implementation of the Merge Sort algorithm.
 *
 * <p>Used to sort a snapshot of the order queue for the dashboard / report
 * view (Alpha 1 / Alpha 2). This is a from-scratch implementation — it does
 * not use {@code Collections.sort}, {@code Arrays.sort}, or any other
 * built-in Java sorting utility, per the project's rule that assessed DSA
 * components must use the team's own implementations.
 *
 * <h2>Complexity</h2>
 * <ul>
 *   <li>Time: O(n log n) in all cases (best, average, worst) — merge sort's
 *       runtime does not depend on the input's initial ordering.</li>
 *   <li>Space: O(n) auxiliary space for the temporary arrays used during
 *       merging.</li>
 *   <li>Stability: Stable — equal elements keep their relative order, which
 *       matters here since two orders can share a priority/timestamp and we
 *       don't want to scramble tie-breaking.</li>
 * </ul>
 */
public final class MergeSort {

    private MergeSort() {
        // Utility class — no instances.
    }

    /**
     * Sorts an array of naturally-ordered elements in ascending order.
     *
     * @param array the array to sort, modified in place
     * @param <T>   element type, must implement {@link Comparable}
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] buffer = (T[]) new Comparable[array.length];
        mergeSort(array, buffer, 0, array.length - 1, Comparator.naturalOrder());
    }

    /**
     * Sorts an array in ascending order according to the given comparator.
     * Use this overload when sorting domain objects (e.g. Order) by a
     * specific field such as priority or placedAt timestamp.
     *
     * @param array      the array to sort, modified in place
     * @param comparator defines the ordering
     * @param <T>        element type
     */
    public static <T> void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length < 2 || comparator == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] buffer = (T[]) new Object[array.length];
        mergeSort(array, buffer, 0, array.length - 1, comparator);
    }

    /**
     * Sorts a {@link List} in place according to the given comparator.
     * Convenience overload for callers holding a queue snapshot as a List
     * (e.g. copied out of Alpha 1's custom Queue/LinkedList) rather than an
     * array.
     *
     * @param list       the list to sort, modified in place
     * @param comparator defines the ordering
     * @param <T>        element type
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() < 2 || comparator == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) list.toArray();
        sort(array, comparator);
        for (int i = 0; i < array.length; i++) {
            list.set(i, array[i]);
        }
    }

    private static <T> void mergeSort(T[] array, T[] buffer, int left, int right,
                                       Comparator<T> comparator) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(array, buffer, left, mid, comparator);
        mergeSort(array, buffer, mid + 1, right, comparator);
        merge(array, buffer, left, mid, right, comparator);
    }

    private static <T> void merge(T[] array, T[] buffer, int left, int mid, int right,
                                   Comparator<T> comparator) {
        // Copy the range being merged into the buffer.
        for (int i = left; i <= right; i++) {
            buffer[i] = array[i];
        }

        int i = left;      // pointer into left half (in buffer)
        int j = mid + 1;   // pointer into right half (in buffer)
        int k = left;      // pointer into array (write position)

        while (i <= mid && j <= right) {
            // "<= 0" (not "< 0") keeps the sort stable: when elements are
            // equal, prefer the one from the left half first.
            if (comparator.compare(buffer[i], buffer[j]) <= 0) {
                array[k++] = buffer[i++];
            } else {
                array[k++] = buffer[j++];
            }
        }
        while (i <= mid) {
            array[k++] = buffer[i++];
        }
        while (j <= right) {
            array[k++] = buffer[j++];
        }
    }
}
