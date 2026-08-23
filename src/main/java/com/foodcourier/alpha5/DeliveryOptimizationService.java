package com.foodcourier.alpha5;

import com.foodcourier.domain.*;
// this is Asare's MergeSort (kelvin-b / feature/mergesort-benchmark branch).
// it's not merged into alpha5 yet, so this won't compile here until it lands —
// see the team message about that.
import com.foodcourier.algorithms.sorting.MergeSort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryOptimizationService {

    // reads data/generated/dummy1.csv and turns each row into a Delivery.
    // header: deliveryId,courierId,courierName,distanceKm,estimatedTimeMinutes
    //
    // Delivery won't let you skip the Order in its constructor, but
    // generateReport() never actually looks inside it, so each row just
    // gets a bare placeholder Order to keep the compiler happy.
    //
    // right now dummy1.csv is empty so this just returns nothing until
    // someone actually puts rows in it.
    public List<Delivery> loadFromCsv(String csvPath) throws IOException {
        List<Delivery> deliveries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String header = reader.readLine();
            if (header == null) {
                return deliveries; // empty file, nothing to load
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");

                String deliveryId = parts[0].trim();
                String courierId = parts[1].trim();
                String courierName = parts[2].trim();
                double distanceKm = Double.parseDouble(parts[3].trim());
                int estimatedTimeMinutes = Integer.parseInt(parts[4].trim());

                Courier courier = new Courier(courierId, courierName, "", CourierStatus.AVAILABLE, null);

                // dummy order, just to satisfy the constructor - generateReport()
                // doesn't touch anything inside it
                Order placeholderOrder = new Order(
                        deliveryId + "-order", null, null, 0.0, Priority.MEDIUM, OrderStatus.DELIVERED);

                Delivery delivery = new Delivery(deliveryId, placeholderOrder, distanceKm, estimatedTimeMinutes);
                delivery.setCourier(courier);
                deliveries.add(delivery);
            }
        }

        return deliveries;
    }

    // grabs avg delivery time, total distance, and orders per courier out of
    // the Delivery records, then ranks couriers by how busy they were.
    public String generateReport(List<Delivery> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) {
            return "No delivery records available to report on.";
        }

        double totalDistance = 0;
        double totalTime = 0;
        Map<String, CourierStats> courierRows = new HashMap<>();

        for (Delivery d : deliveries) {
            totalDistance += d.getDistance();
            totalTime += d.getEstimatedTimeMinutes();

            // courier can be null if a delivery hasn't been assigned yet, so
            // handle that instead of just assuming it's always set
            Courier courier = d.getCourier();
            String courierId = (courier != null) ? courier.getId() : "UNASSIGNED";
            String courierName = (courier != null) ? courier.getName() : "Unassigned";

            courierRows
                .computeIfAbsent(courierId, id -> new CourierStats(courierId, courierName))
                .addDelivery(d);
        }

        double avgTime = totalTime / deliveries.size();

        List<CourierStats> rows = new ArrayList<>(courierRows.values());

        // ranking couriers by order count, busiest first - using Asare's
        // MergeSort here instead of writing another sort from scratch
        MergeSort.sort(rows, Comparator.comparingInt((CourierStats c) -> c.orderCount).reversed());

        StringBuilder sb = new StringBuilder();
        sb.append("=== Delivery Optimization Report ===\n\n");
        sb.append(String.format("Total deliveries: %d%n", deliveries.size()));
        sb.append(String.format("Average delivery time: %.2f minutes%n", avgTime));
        sb.append(String.format("Total distance covered: %.2f km%n%n", totalDistance));
        sb.append("Couriers ranked by order volume:\n");
        sb.append(String.format("%-12s | %-15s | %-8s | %-15s | %-12s%n",
                "Courier ID", "Name", "Orders", "Distance (km)", "Time (min)"));
        sb.append("-".repeat(70)).append("\n");
        for (CourierStats row : rows) {
            sb.append(row).append("\n");
        }

        return sb.toString();
    }

    // one row per courier, tallied up from their deliveries. we sort a list
    // of these instead of raw Deliveries, since "rank couriers" only makes
    // sense once each one's orders are grouped together
    private static class CourierStats implements Comparable<CourierStats> {

        private final String courierId;
        private final String courierName;
        private int orderCount;
        private double totalDistance;
        private double totalTimeMinutes;

        CourierStats(String courierId, String courierName) {
            this.courierId = courierId;
            this.courierName = courierName;
        }

        void addDelivery(Delivery delivery) {
            orderCount++;
            totalDistance += delivery.getDistance();
            totalTimeMinutes += delivery.getEstimatedTimeMinutes();
        }

        // busiest courier first
        @Override
        public int compareTo(CourierStats other) {
            return Integer.compare(other.orderCount, this.orderCount);
        }

        @Override
        public String toString() {
            return String.format("%-12s | %-15s | %-8d | %-15.2f | %-12.1f",
                    courierId, courierName, orderCount, totalDistance, totalTimeMinutes);
        }
    }
}