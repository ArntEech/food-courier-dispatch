package com.foodcourier.algorithms.sorting;

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
        quickSort(orders, 0, orders.size() - 1);
    }

    private static void quickSort(List<Order> orders, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(orders, low, high);
            quickSort(orders, low, pivotIndex - 1);
            quickSort(orders, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Order> orders, int low, int high) {
        Order pivot = orders.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
                if (((Comparable<Object>) orders.get(j).getTimestamp())
                    .compareTo(pivot.getTimestamp()) <= 0) {
                i++;
                swap(orders, i, j);
            }
        }
        swap(orders, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Order> orders, int i, int j) {
        Order temp = orders.get(i);
        orders.set(i, orders.get(j));
        orders.set(j, temp);
    }
}