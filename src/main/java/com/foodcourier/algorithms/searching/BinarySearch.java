package com.foodcourier.algorithms.searching;

import java.util.Comparator;

public final class BinarySearch {

    private BinarySearch() {
    }

    public static <T> int search(T[] sortedArray, T target, Comparator<T> comparator) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        if (!isSorted(sortedArray, comparator)) {
            throw new IllegalArgumentException(
                    "Precondition violated: sortedArray is not sorted according to the given comparator. "
                    + "Binary search requires sorted input - use LinearSearch for unsorted data."
            );
        }

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparator.compare(sortedArray[mid], target);

            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static <T extends Comparable<T>> int search(T[] sortedArray, T target) {
        return search(sortedArray, target, Comparator.naturalOrder());
    }

    public static <T> boolean isSorted(T[] array, Comparator<T> comparator) {
        for (int i = 1; i < array.length; i++) {
            if (comparator.compare(array[i - 1], array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}