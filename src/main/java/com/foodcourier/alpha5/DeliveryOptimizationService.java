package com.foodcourier.alpha5;

import com.foodcourier.domain.*;

// swap in the real sort once it exists:
// import com.foodcourier.algorithms.sorting.QuickSort;
// import com.foodcourier.algorithms.sorting.MergeSort;
// checked Issabella's branch directly (food-courier-dispatch-Issabella-b) —
// QuickSort.java is still just an empty class, nothing to call yet.

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryOptimizationService {

    // reads data/generated/dummy1.csv and turns each row into a Delivery.
    // header: deliveryId,courierId,courierName,distanceKm,estimatedTimeMinutes
    //
    // Delivery's constructor won't let you skip the Order, but generateReport()
    // never actually looks inside it, so each row just gets a bare placeholder
    // Order to keep the domain model happy.
    //
    // right now dummy1.csv is empty, so this just returns an empty list until
    // it's populated — that's expected, not a bug.
    public List<Delivery> loadFromCsv(String csvPath) throws IOException {
        List<Delivery> deliveries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String header = reader.readLine();
            if (header == null) {
                return deliveries; // file's empty, nothing to load
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

                // placeholder order — not used by generateReport(), just here
                // because Delivery requires one
                Order placeholderOrder = new Order(
                        deliveryId + "-order", null, null, 0.0, Priority.MEDIUM, OrderStatus.DELIVERED);

                Delivery delivery = new Delivery(deliveryId, placeholderOrder, distanceKm, estimatedTimeMinutes);
                delivery.setCourier(courier);
                deliveries.add(delivery);
            }
        }

        return deliveries;
    }

    // takes the raw Delivery records and spits out avg time, total distance,
    // and orders-per-courier, then ranks couriers by how many orders they've done.
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

            // a delivery can exist without an assigned courier yet, so don't
            // assume getCourier() is non-null
            Courier courier = d.getCourier();
            String courierId = (courier != null) ? courier.getId() : "UNASSIGNED";
            String courierName = (courier != null) ? courier.getName() : "Unassigned";

            courierRows
                .computeIfAbsent(courierId, id -> new CourierStats(courierId, courierName))
                .addDelivery(d);
        }

        double avgTime = totalTime / deliveries.size();

        List<CourierStats> rows = new ArrayList<>(courierRows.values());

        // this is the "reuse the existing sort" part of the task. right now
        // there IS no existing sort (see import comment above), so this line
        // is a stand-in — rows.sort(null) just falls back to CourierStats's
        // own compareTo. once QuickSort or MergeSort actually has a body,
        // replace this one line with the real call.
        rows.sort(null);

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

    // one row per courier, built up from their Delivery records. this is
    // what actually gets sorted — grouping first is what makes "rank
    // couriers" mean something.
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