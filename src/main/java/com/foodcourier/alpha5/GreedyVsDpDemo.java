package com.foodcourier.alpha5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Standalone demo for comparing the Greedy algorithm with
 * Dynamic Programming using dummy delivery orders.
 *
 * This is not a unit test.
 * Run this class manually to generate performance results.
 */
public class GreedyVsDpDemo {

    public static void main(String[] args) {

        int capacity = 20;

        // Generate reproducible dummy delivery data
        List<Greedy.Order> orders =
                generateDummyOrders(12, capacity);

        System.out.println(
                "=== Dummy orders (capacity = "
                        + capacity + ") ==="
        );

        orders.forEach(System.out::println);

        // -------------------------------------------------
        // GREEDY
        // -------------------------------------------------

        long startGreedy = System.nanoTime();

        List<Greedy.Order> greedyBatch =
                Greedy.earliestDeadlineBatch(
                        orders,
                        capacity
                );

        long greedyTimeNs =
                System.nanoTime() - startGreedy;

        // -------------------------------------------------
        // DP
        // -------------------------------------------------

        long startDp = System.nanoTime();

        List<Greedy.Order> dpBatch =
                dpBatch(orders, capacity);

        long dpTimeNs =
                System.nanoTime() - startDp;

        // -------------------------------------------------
        // DISPLAY GREEDY RESULTS
        // -------------------------------------------------

        System.out.println(
                "\n=== Greedy (Earliest-Deadline-First) ==="
        );

        greedyBatch.forEach(System.out::println);

        System.out.printf(
                "Total value: %d | Total weight: %d | Time: %d ns%n",
                Greedy.totalValue(greedyBatch),
                Greedy.totalWeight(greedyBatch),
                greedyTimeNs
        );

        // -------------------------------------------------
        // DISPLAY DP RESULTS
        // -------------------------------------------------

        System.out.println(
                "\n=== DP (0/1 Knapsack) ==="
        );

        dpBatch.forEach(System.out::println);

        System.out.printf(
                "Total value: %d | Total weight: %d | Time: %d ns%n",
                Greedy.totalValue(dpBatch),
                Greedy.totalWeight(dpBatch),
                dpTimeNs
        );

        // -------------------------------------------------
        // COMPARISON
        // -------------------------------------------------

        int greedyValue =
                Greedy.totalValue(greedyBatch);

        int dpValue =
                Greedy.totalValue(dpBatch);

        System.out.println(
                "\n=== Comparison ==="
        );

        System.out.println(
                "Greedy value: " + greedyValue
        );

        System.out.println(
                "DP value: " + dpValue
        );

        System.out.println(
                "Greedy execution time: "
                        + greedyTimeNs + " ns"
        );

        System.out.println(
                "DP execution time: "
                        + dpTimeNs + " ns"
        );

        if (dpValue > greedyValue) {
            System.out.println(
                    "DP produced a higher-value batch."
            );
        } else if (dpValue == greedyValue) {
            System.out.println(
                    "Greedy and DP produced the same total value."
            );
        } else {
            System.out.println(
                    "Greedy produced a higher value for this dataset."
            );
        }
    }

    /**
     * Generates dummy delivery orders.
     *
     * A fixed random seed is used so that the same data
     * is generated every time the program runs.
     */
    private static List<Greedy.Order> generateDummyOrders(
            int count,
            int capacity) {

        Random rand = new Random(42);

        List<Greedy.Order> orders =
                new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            int weight =
                    1 + rand.nextInt(capacity / 2);

            int value =
                    5 + rand.nextInt(50);

            int deadline =
                    1 + rand.nextInt(count);

            orders.add(
                    new Greedy.Order(
                            "O" + i,
                            weight,
                            value,
                            deadline
                    )
            );
        }

        return orders;
    }

    /**
     * Temporary 0/1 Knapsack Dynamic Programming implementation.
     *
     * This is included so the comparison can run before
     * Francis's DynamicProgramming class is merged.
     *
     * Later, this method can be replaced with a call to
     * Francis's DynamicProgramming implementation.
     */
    private static List<Greedy.Order> dpBatch(
            List<Greedy.Order> orders,
            int capacity) {

        int n = orders.size();

        int[][] dp =
                new int[n + 1][capacity + 1];

        // Build the DP table
        for (int i = 1; i <= n; i++) {

            Greedy.Order order =
                    orders.get(i - 1);

            for (int w = 0; w <= capacity; w++) {

                // Do not select the current order
                dp[i][w] =
                        dp[i - 1][w];

                // Select the current order if it fits
                if (order.weight <= w) {

                    dp[i][w] =
                            Math.max(
                                    dp[i][w],
                                    dp[i - 1][
                                            w - order.weight
                                    ] + order.value
                            );
                }
            }
        }

        // Backtrack through the DP table
        // to find the selected orders.
        List<Greedy.Order> chosen =
                new ArrayList<>();

        int w = capacity;

        for (int i = n; i > 0; i--) {

            if (dp[i][w] != dp[i - 1][w]) {

                Greedy.Order order =
                        orders.get(i - 1);

                chosen.add(order);

                w -= order.weight;
            }
        }

        return chosen;
    }
}