package com.foodcourier.algorithms.optimization;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Programming implementation of the 0/1 Knapsack problem
 * for delivery order batching under capacity constraints.
 *
 * This provides an optimal solution by considering all possible
 * subsets of orders, trading higher time complexity (O(n*W)) for
 * a guaranteed optimal result.
 */
public class DynamicProgramming {

    /**
     * Represents a deliverable item with weight, value, and identifier.
     */
    public static class Item {
        public final String id;
        public final int weight;
        public final int value;

        public Item(String id, int weight, int value) {
            this.id = id;
            this.weight = weight;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format(
                    "Item{id=%s, weight=%d, value=%d}",
                    id, weight, value
            );
        }
    }

    /**
     * Solves the 0/1 Knapsack problem using dynamic programming.
     *
     * @param items    list of items to consider
     * @param capacity maximum weight capacity
     * @return list of selected items that maximize value without exceeding capacity
     */
    public static List<Item> knapsack(List<Item> items, int capacity) {
        if (items == null || items.isEmpty() || capacity <= 0) {
            return new ArrayList<>();
        }

        int n = items.size();

        // DP table: dp[i][w] = maximum value using first i items with capacity w
        int[][] dp = new int[n + 1][capacity + 1];

        // Fill the DP table
        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);

            for (int w = 0; w <= capacity; w++) {
                // Option 1: Don't take the item
                dp[i][w] = dp[i - 1][w];

                // Option 2: Take the item (if it fits)
                if (item.weight <= w) {
                    dp[i][w] = Math.max(
                            dp[i][w],
                            dp[i - 1][w - item.weight] + item.value
                    );
                }
            }
        }

        // Backtrack to find which items were selected
        List<Item> selected = new ArrayList<>();
        int w = capacity;

        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Item item = items.get(i - 1);
                selected.add(item);
                w -= item.weight;
            }
        }

        return selected;
    }

    /**
     * Returns the maximum value achievable for the given items and capacity.
     */
    public static int maxValue(List<Item> items, int capacity) {
        if (items == null || items.isEmpty() || capacity <= 0) {
            return 0;
        }

        int n = items.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Item item = items.get(i - 1);

            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];

                if (item.weight <= w) {
                    dp[i][w] = Math.max(
                            dp[i][w],
                            dp[i - 1][w - item.weight] + item.value
                    );
                }
            }
        }

        return dp[n][capacity];
    }

    /**
     * Calculates total value of selected items.
     */
    public static int totalValue(List<Item> items) {
        int sum = 0;

        for (Item item : items) {
            sum += item.value;
        }

        return sum;
    }

    /**
     * Calculates total weight of selected items.
     */
    public static int totalWeight(List<Item> items) {
        int sum = 0;

        for (Item item : items) {
            sum += item.weight;
        }

        return sum;
    }
}
