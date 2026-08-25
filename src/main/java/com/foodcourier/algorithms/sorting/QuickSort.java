package com.foodcourier.algorithms.sorting;

import java.util.Comparator;
import java.util.List;

import com.foodcourier.domain.Order;

public class QuickSort {

    /**
     * Sorts a list of orders in-place by timestamp in ascending order.
     */
    public static void sort(List<Order> orders) {
        if (orders == null || orders.size() <= 1) {
            return;
        }
        quickSort(orders, 0, orders.size() - 1, Comparator.comparing(Order::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Sorts a list in-place according to the given comparator.
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
        quickSortArray(array, 0, array.length - 1, comparator);
        for (int i = 0; i < array.length; i++) {
            list.set(i, array[i]);
        }
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
        quickSortArray(array, 0, array.length - 1, Comparator.naturalOrder());
    }

    /**
     * Sorts an array in ascending order according to the given comparator.
     *
     * @param array      the array to sort, modified in place
     * @param comparator defines the ordering
     * @param <T>        element type
     */
    public static <T> void sort(T[] array, Comparator<T> comparator) {
        if (array == null || array.length < 2 || comparator == null) {
            return;
        }
        quickSortArray(array, 0, array.length - 1, comparator);
    }

    private static <T> void quickSortArray(T[] array, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partitionArray(array, low, high, comparator);
            quickSortArray(array, low, pivotIndex - 1, comparator);
            quickSortArray(array, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partitionArray(T[] array, int low, int high, Comparator<T> comparator) {
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

    private static void quickSort(List<Order> orders, int low, int high, Comparator<Order> comparator) {
        if (low < high) {
            int pivotIndex = partition(orders, low, high, comparator);
            quickSort(orders, low, pivotIndex - 1, comparator);
            quickSort(orders, pivotIndex + 1, high, comparator);
        }
    }

    private static int partition(List<Order> orders, int low, int high, Comparator<Order> comparator) {
        Order pivot = orders.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(orders.get(j), pivot) <= 0) {
                i++;
                swap(orders, i, j);
            }
        }
        swap(orders, i + 1, high);
        return i + 1;
    }

    private static int compareTimestamps(Order first, Order second) {
        if (first.getTimestamp() == null) {
            return second.getTimestamp() == null ? 0 : 1;
        }
        if (second.getTimestamp() == null) {
            return -1;
        }
        return first.getTimestamp().compareTo(second.getTimestamp());
    }

    private static void swap(List<Order> orders, int i, int j) {
        Order temp = orders.get(i);
        orders.set(i, orders.get(j));
        orders.set(j, temp);
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
