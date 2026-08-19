package com.foodcourier.algorithms.optimization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Delivery;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;

/**
 * Greedy heuristics for the Alpha 5 optimisation layer.
 *
 * Two independent strategies live here:
 *
 * <ul>
 *   <li>{@link #batchDeliveries(List, int)} — knapsack-style order
 *   batching. This is the greedy counterpart to Francis's
 *   {@code DynamicProgramming.java}, which is expected to solve the same
 *   "pack orders into a courier's time budget" problem optimally via
 *   tabulation/memoisation. This one sorts candidates by value-per-minute
 *   and takes greedily until the time budget runs out — fast, but not
 *   guaranteed optimal. See {@code GREEDY_VS_DP.md} in this package for
 *   the full write-up and a constructed case where it loses to DP.</li>
 *
 *   <li>{@link #assignNearestCourier(Order, List)} — nearest-available-
 *   courier dispatch. For an incoming order, hand it to whichever
 *   {@code AVAILABLE} courier is currently closest (straight-line
 *   distance) to the pickup point. No look-ahead, no rebalancing.</li>
 * </ul>
 *
 * Both are greedy in the textbook sense: make the locally-best choice at
 * each step and never revisit it.
 */
public class Greedy {

    /** Mean radius of the Earth in kilometres, used for distance estimates. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    private Greedy() {
        // static utility class — not meant to be instantiated
    }

    /**
     * Greedily selects which deliveries a single courier should take on
     * within a fixed time budget, aiming to maximise total order value.
     *
     * <p>This is the 0/1 knapsack problem — weight = {@code
     * estimatedTimeMinutes}, value = the delivery's order value — solved
     * with the classic greedy heuristic: sort candidates by value-per-
     * minute ("density") descending, then take each one that still fits
     * in the remaining budget, skipping ones that don't (no backtracking).
     * Runs in O(n log n).
     *
     * <p><b>Not guaranteed optimal.</b> Unlike a DP tabulation over the
     * capacity, this can leave a courier's time budget under-filled with
     * lower total value than the best achievable combination — see
     * {@code GreedyTest#batchDeliveries_canLoseToOptimalOnClassicCounterexample()}
     * for a worked example.
     *
     * @param deliveries      candidate deliveries not yet assigned to a batch
     * @param capacityMinutes the courier's remaining time budget, in minutes
     * @return a {@link BatchResult} holding the selected deliveries and totals
     * @throws IllegalArgumentException if deliveries is null or capacityMinutes is negative
     */
    public static BatchResult batchDeliveries(List<Delivery> deliveries, int capacityMinutes) {
        if (deliveries == null) {
            throw new IllegalArgumentException("deliveries cannot be null");
        }
        if (capacityMinutes < 0) {
            throw new IllegalArgumentException("capacityMinutes cannot be negative");
        }

        List<Delivery> candidates = new ArrayList<>(deliveries);
        candidates.sort(Comparator.comparingDouble(Greedy::valueDensity).reversed());

        List<Delivery> selected = new ArrayList<>();
        int remaining = capacityMinutes;
        double totalValue = 0.0;

        for (Delivery delivery : candidates) {
            int cost = delivery.getEstimatedTimeMinutes();
            if (cost >= 0 && cost <= remaining) {
                selected.add(delivery);
                remaining -= cost;
                totalValue += orderValue(delivery);
            }
        }

        return new BatchResult(selected, totalValue, capacityMinutes - remaining);
    }

    /**
     * Assigns an order to whichever {@code AVAILABLE} courier is currently
     * closest (straight-line distance) to the order's pickup location.
     *
     * <p>Couriers that are {@code BUSY}, {@code OFFLINE}, or missing a
     * current location are skipped. This is a purely local, single-order
     * decision — it does not consider what other orders might arrive next
     * or whether a farther courier would be better positioned overall.
     *
     * @param order    the order to be picked up, must have a restaurant with a location
     * @param couriers the pool of couriers to choose from
     * @return the nearest available courier, or {@code null} if none qualify
     * @throws IllegalArgumentException if order is null or has no pickup location
     */
    public static Courier assignNearestCourier(Order order, List<Courier> couriers) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }
        if (order.getRestaurant() == null || order.getRestaurant().getLocation() == null) {
            throw new IllegalArgumentException("order must have a restaurant with a pickup location");
        }
        if (couriers == null || couriers.isEmpty()) {
            return null;
        }

        Location pickup = order.getRestaurant().getLocation();

        Courier nearest = null;
        double nearestDistanceKm = Double.POSITIVE_INFINITY;

        for (Courier courier : couriers) {
            if (courier.getStatus() != CourierStatus.AVAILABLE) {
                continue;
            }
            Location current = courier.getCurrentLocation();
            if (current == null) {
                continue;
            }

            double distanceKm = haversineKm(current, pickup);
            if (distanceKm < nearestDistanceKm) {
                nearestDistanceKm = distanceKm;
                nearest = courier;
            }
        }

        return nearest;
    }

    // ---- helpers ----

    /** Value per minute of estimated delivery time — what batching sorts by. */
    private static double valueDensity(Delivery delivery) {
        int cost = delivery.getEstimatedTimeMinutes();
        double value = orderValue(delivery);
        if (cost <= 0) {
            // zero/negative-cost deliveries are effectively free — always worth taking
            return value > 0 ? Double.POSITIVE_INFINITY : 0.0;
        }
        return value / cost;
    }

    private static double orderValue(Delivery delivery) {
        Order order = delivery.getOrder();
        return order != null ? order.getValue() : 0.0;
    }

    /** Great-circle distance between two locations, in kilometres. */
    private static double haversineKm(Location a, Location b) {
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());

        double sinDLat = Math.sin(dLat / 2);
        double sinDLon = Math.sin(dLon / 2);
        double h = sinDLat * sinDLat + Math.cos(lat1) * Math.cos(lat2) * sinDLon * sinDLon;
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));

        return EARTH_RADIUS_KM * c;
    }

    /** Result of a {@link #batchDeliveries(List, int)} call. */
    public static final class BatchResult {

        private final List<Delivery> selectedDeliveries;
        private final double totalValue;
        private final int totalTimeMinutes;

        BatchResult(List<Delivery> selectedDeliveries, double totalValue, int totalTimeMinutes) {
            this.selectedDeliveries = List.copyOf(selectedDeliveries);
            this.totalValue = totalValue;
            this.totalTimeMinutes = totalTimeMinutes;
        }

        public List<Delivery> getSelectedDeliveries() {
            return selectedDeliveries;
        }

        public double getTotalValue() {
            return totalValue;
        }

        public int getTotalTimeMinutes() {
            return totalTimeMinutes;
        }

        @Override
        public String toString() {
            return "BatchResult{" +
                    "deliveries=" + selectedDeliveries.size() +
                    ", totalValue=" + totalValue +
                    ", totalTimeMinutes=" + totalTimeMinutes +
                    '}';
        }
    }
}
