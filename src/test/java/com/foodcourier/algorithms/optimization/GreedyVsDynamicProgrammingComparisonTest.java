package com.foodcourier.algorithms.optimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Delivery;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

/**
 * Not a correctness test for Greedy (see {@link GreedyTest} for that) —
 * this produces the numbers behind {@code GREEDY_VS_DP.md}: how close
 * Greedy's batching gets to the true optimum, and how the two scale with
 * input size, on generated dummy delivery data.
 *
 * <p>{@code DynamicProgramming.java} (Francis's task) is still an empty
 * stub at the time this was written, so {@link #referenceDpOptimalValue}
 * below is a small local 0/1 knapsack tabulation used only to produce a
 * DP baseline for this comparison — it is NOT the project's canonical DP
 * implementation. Swap the call for the real one once it exists; the
 * problem shape is identical (weight = estimatedTimeMinutes, value =
 * order value, capacity = courier time budget), so the numbers should
 * not move.
 */
class GreedyVsDynamicProgrammingComparisonTest {

    @Test
    void greedyVsDp_qualityAndTimingOnDummyDeliveryData() {
        report(50, 240);
        report(200, 240);
        report(500, 480);
    }

    private void report(int deliveryCount, int capacityMinutes) {
        List<Delivery> deliveries = generateDummyDeliveries(deliveryCount, 42L);

        long greedyStart = System.nanoTime();
        Greedy.BatchResult greedyResult = Greedy.batchDeliveries(deliveries, capacityMinutes);
        long greedyNanos = System.nanoTime() - greedyStart;

        long dpStart = System.nanoTime();
        double dpOptimalValue = referenceDpOptimalValue(deliveries, capacityMinutes);
        long dpNanos = System.nanoTime() - dpStart;

        double gapPercent = dpOptimalValue == 0
                ? 0.0
                : 100.0 * (dpOptimalValue - greedyResult.getTotalValue()) / dpOptimalValue;

        System.out.printf(
                "n=%-4d capacity=%-4d | greedy: value=%8.2f time=%7.3fms | dp: value=%8.2f time=%9.3fms | gap=%.2f%%%n",
                deliveryCount, capacityMinutes,
                greedyResult.getTotalValue(), greedyNanos / 1e6,
                dpOptimalValue, dpNanos / 1e6,
                gapPercent);

        // Property that must always hold: DP is provably optimal for 0/1
        // knapsack, so it can never do worse than the greedy heuristic.
        assertTrue(dpOptimalValue >= greedyResult.getTotalValue() - 1e-9,
                "DP's optimal value should never be less than greedy's");
    }

    /**
     * Local 0/1 knapsack tabulation — see class Javadoc. Not the
     * project's DynamicProgramming.java.
     */
    private static double referenceDpOptimalValue(List<Delivery> deliveries, int capacityMinutes) {
        int n = deliveries.size();
        double[][] dp = new double[n + 1][capacityMinutes + 1];

        for (int i = 1; i <= n; i++) {
            Delivery delivery = deliveries.get(i - 1);
            int weight = Math.max(0, Math.min(delivery.getEstimatedTimeMinutes(), capacityMinutes));
            double value = delivery.getOrder() != null ? delivery.getOrder().getValue() : 0.0;

            for (int cap = 0; cap <= capacityMinutes; cap++) {
                dp[i][cap] = dp[i - 1][cap];
                if (weight <= cap) {
                    dp[i][cap] = Math.max(dp[i][cap], dp[i - 1][cap - weight] + value);
                }
            }
        }

        return dp[n][capacityMinutes];
    }

    private static List<Delivery> generateDummyDeliveries(int count, long seed) {
        Random random = new Random(seed);
        List<Delivery> deliveries = new ArrayList<>(count);

        Location restaurantLocation = new Location("RL", "Dummy Restaurant Location", 5.65, -0.18);
        Location customerLocation = new Location("CL", "Dummy Customer Location", 5.66, -0.19);
        Restaurant restaurant = new Restaurant("R-DUMMY", "Dummy Restaurant", restaurantLocation);
        Customer customer = new Customer("CU-DUMMY", "Dummy Customer", "0000000000", customerLocation);

        for (int i = 0; i < count; i++) {
            double value = 10 + random.nextDouble() * 90;     // GHS 10 - 100
            int timeMinutes = 5 + random.nextInt(56);          // 5 - 60 minutes
            double distanceKm = 0.5 + random.nextDouble() * 4.5;

            Order order = new Order("O-" + i, customer, restaurant, value, Priority.MEDIUM, OrderStatus.RECEIVED);
            deliveries.add(new Delivery("D-" + i, order, distanceKm, timeMinutes));
        }

        return deliveries;
    }
}
