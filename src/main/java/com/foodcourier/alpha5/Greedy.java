package com.foodcourier.alpha5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Greedy strategy for batching orders onto a single courier trip
 * under a capacity constraint.
 *
 * Strategy used: EARLIEST-DEADLINE-FIRST.
 * Orders are sorted by deadline (soonest first) and added to the
 * batch as long as the courier's remaining capacity allows it.
 */
public class Greedy {

    /**
     * Represents a single deliverable order.
     */
    public static class Order {
        public final String id;
        public final int weight;
        public final int value;
        public final int deadline;

        public Order(String id, int weight, int value, int deadline) {
            this.id = id;
            this.weight = weight;
            this.value = value;
            this.deadline = deadline;
        }

        @Override
        public String toString() {
            return String.format(
                    "Order{id=%s, weight=%d, value=%d, deadline=%d}",
                    id, weight, value, deadline
            );
        }
    }

    /**
     * Greedily selects orders for one courier based on
     * earliest deadline first.
     *
     * @param orders candidate orders
     * @param capacity maximum courier capacity
     * @return selected orders
     */
    public static List<Order> earliestDeadlineBatch(
            List<Order> orders, int capacity) {

        if (orders == null || orders.isEmpty() || capacity <= 0) {
            return new ArrayList<>();
        }

        // Make a copy so the original list is not changed
        List<Order> sorted = new ArrayList<>(orders);

        // Sort orders by earliest deadline
        sorted.sort(Comparator.comparingInt(o -> o.deadline));

        List<Order> batch = new ArrayList<>();
        int usedCapacity = 0;

        // Greedily add orders while capacity allows
        for (Order order : sorted) {
            if (usedCapacity + order.weight <= capacity) {
                batch.add(order);
                usedCapacity += order.weight;
            }
        }

        return batch;
    }

    /**
     * Calculates the total value of the selected orders.
     */
    public static int totalValue(List<Order> batch) {
        int sum = 0;

        for (Order order : batch) {
            sum += order.value;
        }

        return sum;
    }

    /**
     * Calculates the total weight of the selected orders.
     */
    public static int totalWeight(List<Order> batch) {
        int sum = 0;

        for (Order order : batch) {
            sum += order.weight;
        }

        return sum;
    }
}