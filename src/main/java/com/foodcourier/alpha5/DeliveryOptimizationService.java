package alpha5;

import com.foodcourier.algorithms.optimization.DynamicProgramming;
import com.foodcourier.algorithms.optimization.DynamicProgramming.BatchResult;
import com.foodcourier.algorithms.optimization.DynamicProgramming.Order;

import java.util.List;

/**
 * Stub service for delivery route optimization.
 * Real logic (DP knapsack batching) lives in DynamicProgramming; this class
 * is the integration point Kofi's code depends on.
 *
 * TODO(Francis): implement optimize() and generateReport() below.
 */
public class DeliveryOptimizationService {

    /** A single delivery/order to be assigned to a courier. */
    public static class Delivery {
        public final String id;
        public final int weight;              // capacity units (bag/volume slots)
        public final int priority;             // value/priority score
        public final int estimatedTimeMinutes; // prep + travel estimate

        public Delivery(String id, int weight, int priority, int estimatedTimeMinutes) {
            this.id = id;
            this.weight = weight;
            this.priority = priority;
            this.estimatedTimeMinutes = estimatedTimeMinutes;
        }
    }

    /** A courier available to take on a batch of deliveries. */
    public static class Courier {
        public final String id;
        public final int capacity;       // max bag/volume capacity
        public final int maxTimeMinutes; // time budget before shift/window ends

        public Courier(String id, int capacity, int maxTimeMinutes) {
            this.id = id;
            this.capacity = capacity;
            this.maxTimeMinutes = maxTimeMinutes;
        }
    }

    /** A candidate route: an ordered set of deliveries to be optimized against couriers. */
    public static class Route {
        public final String routeId;
        public final List<Delivery> deliveries;

        public Route(String routeId, List<Delivery> deliveries) {
            this.routeId = routeId;
            this.deliveries = deliveries;
        }
    }

    /** Result returned by optimize(): which deliveries got assigned, and to what value. */
    public static class OptimizationResult {
        public final String courierId;
        public final List<Delivery> assignedDeliveries;
        public final int totalValue;
        public final int totalWeight;

        public OptimizationResult(String courierId, List<Delivery> assignedDeliveries,
                                   int totalValue, int totalWeight) {
            this.courierId = courierId;
            this.assignedDeliveries = assignedDeliveries;
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
        }
    }

    /**
     * Assign the best subset of a route's deliveries to a courier, respecting
     * that courier's capacity and time budget.
     *
     * TODO(Francis): for now this only optimizes against the FIRST courier in
     * the list. Extend to multi-courier assignment (e.g. iterate couriers,
     * removing assigned deliveries from the pool each round) once the team
     * agrees on the multi-courier allocation strategy.
     */
    public OptimizationResult optimize(Route route, List<Courier> couriers) {
        if (route == null || route.deliveries == null || route.deliveries.isEmpty()
                || couriers == null || couriers.isEmpty()) {
            return new OptimizationResult(null, List.of(), 0, 0);
        }

        Courier courier = couriers.get(0);

        List<Order> orders = route.deliveries.stream()
                .map(d -> new Order(d.id, d.weight, d.priority, d.estimatedTimeMinutes))
                .toList();

        BatchResult batch = DynamicProgramming.optimizeBatchWithTimeConstraint(
                orders, courier.capacity, courier.maxTimeMinutes);

        List<Delivery> assigned = route.deliveries.stream()
                .filter(d -> batch.selectedOrders.stream().anyMatch(o -> o.id.equals(d.id)))
                .toList();

        return new OptimizationResult(courier.id, assigned, batch.totalValue, batch.totalWeight);
    }

    /**
     * Produce a human-readable summary report for a batch of deliveries.
     *
     * TODO(Francis): decide on final report format with the team (plain text
     * vs JSON vs the shared reporting template) before implementing.
     */
    public String generateReport(List<Delivery> deliveries) {
        throw new UnsupportedOperationException(
                "TODO: implement generateReport() - summarize totals, capacity utilization, unassigned orders");
    }
}
