package com.foodcourier.algorithms.optimization;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Delivery;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

class GreedyTest {

    // ---- batchDeliveries ----

    @Test
    void batchDeliveries_selectsHighestDensityItemsWithinCapacity() {
        // density = value / time: D1 = 3.0, D2 = 2.0, D3 = 1.0
        Delivery d1 = delivery("D1", 30.0, 10); // density 3.0
        Delivery d2 = delivery("D2", 20.0, 10); // density 2.0
        Delivery d3 = delivery("D3", 10.0, 10); // density 1.0

        Greedy.BatchResult result = Greedy.batchDeliveries(List.of(d1, d2, d3), 20);

        assertEquals(2, result.getSelectedDeliveries().size());
        assertTrue(result.getSelectedDeliveries().contains(d1));
        assertTrue(result.getSelectedDeliveries().contains(d2));
        assertEquals(50.0, result.getTotalValue(), 0.0001);
        assertEquals(20, result.getTotalTimeMinutes());
    }

    @Test
    void batchDeliveries_skipsItemsThatDontFitEvenIfDense() {
        // Highest density item costs more than the whole budget, so it's skipped
        // and a lower-density item that fits gets taken instead.
        Delivery expensive = delivery("EXP", 100.0, 15); // density ~6.67, doesn't fit in 10
        Delivery cheap = delivery("CHEAP", 5.0, 5);       // density 1.0, fits

        Greedy.BatchResult result = Greedy.batchDeliveries(List.of(expensive, cheap), 10);

        assertEquals(List.of(cheap), result.getSelectedDeliveries());
        assertEquals(5.0, result.getTotalValue(), 0.0001);
        assertEquals(5, result.getTotalTimeMinutes());
    }

    @Test
    void batchDeliveries_zeroCapacitySelectsNothingWithPositiveCost() {
        Delivery d1 = delivery("D1", 30.0, 10);

        Greedy.BatchResult result = Greedy.batchDeliveries(List.of(d1), 0);

        assertTrue(result.getSelectedDeliveries().isEmpty());
        assertEquals(0.0, result.getTotalValue(), 0.0001);
        assertEquals(0, result.getTotalTimeMinutes());
    }

    @Test
    void batchDeliveries_emptyCandidateListReturnsEmptyResult() {
        Greedy.BatchResult result = Greedy.batchDeliveries(List.of(), 60);

        assertTrue(result.getSelectedDeliveries().isEmpty());
        assertEquals(0.0, result.getTotalValue(), 0.0001);
    }

    @Test
    void batchDeliveries_throwsOnNullDeliveries() {
        assertThrows(IllegalArgumentException.class, () -> Greedy.batchDeliveries(null, 60));
    }

    @Test
    void batchDeliveries_throwsOnNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> Greedy.batchDeliveries(List.of(), -1));
    }

    /**
     * Classic textbook counterexample showing the greedy density heuristic
     * is NOT optimal for 0/1 knapsack, unlike a DP tabulation.
     *
     * Capacity = 50:
     *  - A: time 10, value 60  (density 6.0)
     *  - B: time 20, value 100 (density 5.0)
     *  - C: time 30, value 120 (density 4.0)
     *
     * Greedy (by density) takes A then B (30 minutes, value 160) — C no
     * longer fits in the remaining 20 minutes. The true optimum is B + C
     * (50 minutes exactly, value 220), which greedy never considers
     * because it commits to A first and never backtracks.
     */
    @Test
    void batchDeliveries_canLoseToOptimalOnClassicCounterexample() {
        Delivery a = delivery("A", 60.0, 10);
        Delivery b = delivery("B", 100.0, 20);
        Delivery c = delivery("C", 120.0, 30);

        Greedy.BatchResult greedyResult = Greedy.batchDeliveries(List.of(a, b, c), 50);

        // Greedy's actual (suboptimal) result:
        assertEquals(List.of(a, b), greedyResult.getSelectedDeliveries());
        assertEquals(160.0, greedyResult.getTotalValue(), 0.0001);

        // The true optimum (B + C) is strictly better and DP would find it:
        double optimalValue = b.getOrder().getValue() + c.getOrder().getValue();
        assertEquals(220.0, optimalValue, 0.0001);
        assertTrue(optimalValue > greedyResult.getTotalValue(),
                "greedy should be strictly worse than the optimal B+C combination");
    }

    // ---- assignNearestCourier ----

    @Test
    void assignNearestCourier_picksClosestAvailableCourier() {
        Location pickup = location("PICKUP", 5.6500, -0.1870);

        Courier near = courier("C-NEAR", CourierStatus.AVAILABLE, location("L1", 5.6502, -0.1871));
        Courier far = courier("C-FAR", CourierStatus.AVAILABLE, location("L2", 5.7000, -0.2200));

        Order order = order("O1", pickup);

        Courier chosen = Greedy.assignNearestCourier(order, List.of(far, near));

        assertEquals(near, chosen);
    }

    @Test
    void assignNearestCourier_skipsBusyAndOfflineCouriers() {
        Location pickup = location("PICKUP", 5.6500, -0.1870);

        // Closer, but not available — should be skipped.
        Courier busyButClose = courier("C-BUSY", CourierStatus.BUSY, location("L1", 5.6501, -0.1870));
        Courier offlineButClose = courier("C-OFFLINE", CourierStatus.OFFLINE, location("L2", 5.6501, -0.1871));
        Courier availableButFar = courier("C-FAR", CourierStatus.AVAILABLE, location("L3", 5.8000, -0.3000));

        Order order = order("O1", pickup);

        Courier chosen = Greedy.assignNearestCourier(order, List.of(busyButClose, offlineButClose, availableButFar));

        assertEquals(availableButFar, chosen);
    }

    @Test
    void assignNearestCourier_returnsNullWhenNoAvailableCouriers() {
        Location pickup = location("PICKUP", 5.6500, -0.1870);
        Courier busy = courier("C-BUSY", CourierStatus.BUSY, location("L1", 5.6501, -0.1870));

        Order order = order("O1", pickup);

        assertNull(Greedy.assignNearestCourier(order, List.of(busy)));
    }

    @Test
    void assignNearestCourier_returnsNullForEmptyOrNullCourierList() {
        Order order = order("O1", location("PICKUP", 5.6500, -0.1870));

        assertNull(Greedy.assignNearestCourier(order, List.of()));
        assertNull(Greedy.assignNearestCourier(order, null));
    }

    @Test
    void assignNearestCourier_throwsWhenOrderHasNoPickupLocation() {
        Order order = order("O1", null);

        assertThrows(IllegalArgumentException.class, () -> Greedy.assignNearestCourier(order, List.of()));
    }

    @Test
    void assignNearestCourier_throwsOnNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> Greedy.assignNearestCourier(null, List.of()));
    }

    // ---- fixtures ----

    private static Location location(String id, double lat, double lon) {
        return new Location(id, "Location " + id, lat, lon);
    }

    private static Restaurant restaurant(String id, Location location) {
        return new Restaurant(id, "Restaurant " + id, location);
    }

    private static Order order(String id, Location pickupLocation) {
        Restaurant restaurant = pickupLocation != null ? restaurant("R-" + id, pickupLocation) : null;
        return new Order(id, null, restaurant, 0.0, Priority.MEDIUM, OrderStatus.RECEIVED);
    }

    private static Order order(String id, double value) {
        Restaurant restaurant = restaurant("R-" + id, location("RL-" + id, 5.65, -0.18));
        Customer customer = new Customer("CU-" + id, "Customer " + id, "0000000000",
                location("CL-" + id, 5.66, -0.19));
        return new Order(id, customer, restaurant, value, Priority.MEDIUM, OrderStatus.RECEIVED);
    }

    private static Delivery delivery(String id, double orderValue, int estimatedTimeMinutes) {
        Order order = order("O-" + id, orderValue);
        return new Delivery(id, order, 1.0, estimatedTimeMinutes);
    }

    private static Courier courier(String id, CourierStatus status, Location currentLocation) {
        return new Courier(id, "Courier " + id, "0000000000", status, currentLocation);
    }
}
