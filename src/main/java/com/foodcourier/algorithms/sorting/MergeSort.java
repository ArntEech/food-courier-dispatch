package com.foodcourier.algorithms.sorting;

import java.util.Comparator;
import java.util.List;

public class MergeSort {

    // Sort arrays using natural ordering
    public static <T extends Comparable<? super T>> void sort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        sort(array, 0, array.length - 1, Comparator.naturalOrder());
    }

    // Sort arrays using a custom comparator
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        if (array == null || array.length <= 1) {
            return;
        }

        // The test expects null comparator to leave the array unchanged
        if (comparator == null) {
            return;
        }

        sort(array, 0, array.length - 1, comparator);
    }

    // Recursive merge sort
    private static <T> void sort(
            T[] array,
            int left,
            int right,
            Comparator<? super T> comparator) {

        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        sort(array, left, middle, comparator);
        sort(array, middle + 1, right, comparator);

        merge(array, left, middle, right, comparator);
    }

    // Merge two sorted sections
    private static <T> void merge(
            T[] array,
            int left,
            int middle,
            int right,
            Comparator<? super T> comparator) {

        Object[] temp = new Object[right - left + 1];

        int i = left;
        int j = middle + 1;
        int k = 0;

        while (i <= middle && j <= right) {

            // <= is important because it keeps MergeSort stable
            if (comparator.compare(array[i], array[j]) <= 0) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        while (i <= middle) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        for (int x = 0; x < temp.length; x++) {
            @SuppressWarnings("unchecked")
            T value = (T) temp[x];

            array[left + x] = value;
        }
    }

    // Sort a List in place using a comparator
    public static <T> void sort(
            List<T> list,
            Comparator<? super T> comparator) {

        if (list == null || list.size() <= 1) {
            return;
        }

        if (comparator == null) {
            return;
        }

        Object[] array = list.toArray();

        @SuppressWarnings("unchecked")
        T[] temp = (T[]) array;

        sort(temp, comparator);

        for (int i = 0; i < temp.length; i++) {
            list.set(i, temp[i]);
        }
    }
}