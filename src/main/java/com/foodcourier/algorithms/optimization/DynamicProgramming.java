package com.foodcourier.algorithms.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 0/1 knapsack DP for batching food orders onto a single courier run.
 * "Weight" is capacity consumed (e.g. bag slots / volume units) and
 * "value" is the priority/profit score of fulfilling that order.
 */
public class DynamicProgramming {

    /** A single order that could be assigned to a courier. */
    public static class Order {
        public final String id;
        public final int weight;            // capacity units consumed
        public final int value;             // priority / profit score
        public final int prepTimeMinutes;   // optional, used by time-constrained variant

        public Order(String id, int weight, int value, int prepTimeMinutes) {
            this.id = id;
            this.weight = weight;
            this.value = value;
            this.prepTimeMinutes = prepTimeMinutes;
        }

        @Override
        public String toString() {
            return "Order{" + id + ", w=" + weight + ", v=" + value + "}";
        }
    }

    /** Result of an optimal batch selection. */
    public static class BatchResult {
        public final List<Order> selectedOrders;
        public final int totalValue;
        public final int totalWeight;

        public BatchResult(List<Order> selectedOrders, int totalValue, int totalWeight) {
            this.selectedOrders = selectedOrders;
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
        }
    }

    /**
     * Classic 0/1 knapsack: choose a subset of orders that maximizes total
     * value without exceeding the courier's capacity.
     *
     * @param orders   candidate orders
     * @param capacity courier capacity (in the same units as Order.weight)
     */
    public static BatchResult optimizeBatch(List<Order> orders, int capacity) {
        if (orders == null || orders.isEmpty() || capacity <= 0) {
            return new BatchResult(new ArrayList<>(), 0, 0);
        }

        int n = orders.size();
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Order o = orders.get(i - 1);
            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (o.weight <= w) {
                    int candidate = dp[i - 1][w - o.weight] + o.value;
                    if (candidate > dp[i][w]) {
                        dp[i][w] = candidate;
                    }
                }
            }
        }

        // Backtrack to recover the selected orders
        List<Order> selected = new ArrayList<>();
        int w = capacity;
        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Order o = orders.get(i - 1);
                selected.add(o);
                w -= o.weight;
            }
        }
        Collections.reverse(selected);

        int totalWeight = 0;
        for (Order o : selected) totalWeight += o.weight;

        return new BatchResult(selected, dp[n][capacity], totalWeight);
    }

    /**
     * 2D variant: respects both a capacity constraint (bag/volume) and a
     * total prep-time budget (e.g. courier must depart within X minutes).
     */
    public static BatchResult optimizeBatchWithTimeConstraint(List<Order> orders, int capacity, int maxTimeMinutes) {
        if (orders == null || orders.isEmpty() || capacity <= 0 || maxTimeMinutes <= 0) {
            return new BatchResult(new ArrayList<>(), 0, 0);
        }

        int n = orders.size();
        // dp[i][w][t] = best value using first i orders, weight budget w, time budget t
        int[][][] dp = new int[n + 1][capacity + 1][maxTimeMinutes + 1];

        for (int i = 1; i <= n; i++) {
            Order o = orders.get(i - 1);
            for (int w = 0; w <= capacity; w++) {
                for (int t = 0; t <= maxTimeMinutes; t++) {
                    dp[i][w][t] = dp[i - 1][w][t];
                    if (o.weight <= w && o.prepTimeMinutes <= t) {
                        int candidate = dp[i - 1][w - o.weight][t - o.prepTimeMinutes] + o.value;
                        if (candidate > dp[i][w][t]) {
                            dp[i][w][t] = candidate;
                        }
                    }
                }
            }
        }

        List<Order> selected = new ArrayList<>();
        int w = capacity, t = maxTimeMinutes;
        for (int i = n; i > 0; i--) {
            if (dp[i][w][t] != dp[i - 1][w][t]) {
                Order o = orders.get(i - 1);
                selected.add(o);
                w -= o.weight;
                t -= o.prepTimeMinutes;
            }
        }
        Collections.reverse(selected);

        int totalWeight = 0;
        for (Order o : selected) totalWeight += o.weight;

        return new BatchResult(selected, dp[n][capacity][maxTimeMinutes], totalWeight);
    }
}
