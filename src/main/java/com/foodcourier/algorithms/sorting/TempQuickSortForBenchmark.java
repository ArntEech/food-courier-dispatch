package com.foodcourier.algorithms.sorting;

import java.util.Comparator;

/**
 * PLACEHOLDER.
 *
 * This is a stand-in QuickSort with the same method signatures as
 * {@link MergeSort}, written only so {@link SortBenchmark} compiles and
 * produces a first set of numbers. Delete this file and replace it with
 * Issabella's actual {@code QuickSort.java} once she pushes it — as long as
 * her class exposes the same two static {@code sort(...)} methods used
 * below, the benchmark needs no other changes.
 */
public final class TempQuickSortForBenchmark {

    private TempQuickSortForBenchmark() {
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, Comparator.naturalOrder());
    }

    public static <T> void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length < 2 || comparator == null) {
            return;
        }
        quickSort(array, 0, array.length - 1, comparator);
    }

    private static <T> void quickSort(T[] array, int low, int high, Comparator<T> comparator) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(array, low, high, comparator);
        quickSort(array, low, pivotIndex - 1, comparator);
        quickSort(array, pivotIndex + 1, high, comparator);
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<T> comparator) {
        T pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(array[j], pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] array, int a, int b) {
        T temp = array[a];
        array[a] = array[b];
        array[b] = temp;
    }
}
