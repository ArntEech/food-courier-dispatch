package com.foodcourier.app;

import com.foodcourier.alpha1.OrderIntakeService;
import com.foodcourier.alpha2.DispatchService;
import com.foodcourier.alpha3.CourierAssignmentService;
import com.foodcourier.alpha4.DataLoader;
import com.foodcourier.alpha4.RouteNavigationService;
import com.foodcourier.alpha5.DeliveryOptimizationService;
import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Delivery;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;
import com.foodcourier.dsa.disjointset.DisjointSet;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.dsa.hashtable.HashMapTable;
import com.foodcourier.dsa.tree.BST;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main application entry point — wires all five Alphas together.
 *
 * Flow:
 *   1. Alpha 4 (DataLoader) loads locations + roads into Graph
 *   2. Alpha 1 (OrderIntakeService) loads orders from CSV into FIFO queue
 *   3. Alpha 2 (DispatchService) prioritizes orders via BinaryHeap
 *   4. Alpha 3 (CourierAssignmentService) assigns available couriers via HashMap + BST
 *   5. Alpha 4 (RouteNavigationService) computes courier→restaurant→customer routes
 *   6. Alpha 5 (DeliveryOptimizationService) generates delivery report
 */
public class FoodCourierApplications {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Food Courier Dispatch System ===\n");

        // ---------------------------------------------------------------
        // ALPHA 4 — Load the delivery network (locations + roads)
        // ---------------------------------------------------------------
        System.out.println("[Alpha 4] Loading delivery network...");
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadAllData();
        Graph<Integer> graph = dataLoader.getGraph();

        // Build DisjointSet for Kruskal (Alpha 4 MST)
        com.foodcourier.dsa.disjointset.DisjointSetInterface<Integer> disjointSet =
                new DisjointSet<>();
        for (Integer v : graph.getVertices()) {
            disjointSet.makeSet(v);
        }

        RouteNavigationService routeNav = new RouteNavigationService(graph, disjointSet);
        System.out.println("  Network: " + graph.vertexCount() + " locations, " + graph.edgeCount() + " roads\n");

        // ---------------------------------------------------------------
        // Build domain objects from seed CSVs (customers, restaurants, couriers)
        // ---------------------------------------------------------------
        System.out.println("[Setup] Building domain objects from seed data...");
        List<Customer> customers = loadCustomers(dataLoader.getLocationNames());
        List<Restaurant> restaurants = loadRestaurants(dataLoader.getLocationNames());
        List<Courier> couriers = loadCouriers(dataLoader.getLocationNames());

        // HashMapTable for O(1) courier lookup by ID
        HashMapTable<String, Courier> courierById = new HashMapTable<>();
        for (Courier c : couriers) {
            courierById.put(c.getId(), c);
        }

        // BST for sorted courier IDs (used by Alpha 3)
        BST<String> courierIdsSorted = new BST<>();
        for (Courier c : couriers) {
            courierIdsSorted.insert(c.getId());
        }

        System.out.println("  Loaded: " + customers.size() + " customers, "
                + restaurants.size() + " restaurants, " + couriers.size() + " couriers\n");

        // ---------------------------------------------------------------
        // ALPHA 1 — Order Intake (load orders from CSV into queue)
        // ---------------------------------------------------------------
        System.out.println("[Alpha 1] Loading orders into intake queue...");
        OrderIntakeService intake = new OrderIntakeService();
        Path ordersCsv = Paths.get("data/seed/orders.csv");
        intake.loadSeedData(ordersCsv);
        System.out.println("  Orders in queue: " + intake.getQueueSize() + "\n");

        // Build fully-populated orders with real locations from loaded domain objects
        List<Order> allOrders = new ArrayList<>();
        intake.loadSeedData(ordersCsv); // reload for demo
        while (intake.getQueueSize() > 0) {
            Order raw = intake.getNextOrder();
            if (raw != null) {
                String custId = raw.getCustomer().getId();
                String restId = raw.getRestaurant().getId();
                Customer fullCust = customers.stream().filter(c -> c.getId().equals(custId)).findFirst().orElse(null);
                Restaurant fullRest = restaurants.stream().filter(r -> r.getId().equals(restId)).findFirst().orElse(null);
                if (fullCust != null && fullRest != null) {
                    Order fullOrder = new Order(
                            raw.getId(),
                            fullCust,
                            fullRest,
                            raw.getValue(),
                            raw.getPriority(),
                            raw.getStatus(),
                            raw.getTimestamp()
                    );
                    allOrders.add(fullOrder);
                }
            }
        }
        System.out.println("  Built " + allOrders.size() + " fully-populated orders\n");

        // ---------------------------------------------------------------
        // ALPHA 2 — Priority Dispatch (heap-based prioritization)
        // ---------------------------------------------------------------
        System.out.println("[Alpha 2] Prioritizing orders via BinaryHeap...");
        DispatchService dispatch = new DispatchService();

        for (Order order : allOrders) {
            dispatch.insert(order);
        }
        System.out.println("  Orders in dispatch heap: " + dispatch.findOrder("1").isPresent() + " (sample lookup)\n");

        // ---------------------------------------------------------------
        // ALPHA 3 — Courier Assignment (HashMap + BST)
        // ---------------------------------------------------------------
        System.out.println("[Alpha 3] Assigning couriers to orders...");
        List<Order> activeOrders = new ArrayList<>(allOrders);

        CourierAssignmentService assignment = new CourierAssignmentService(
                courierById, courierIdsSorted, activeOrders);

        List<Delivery> completedDeliveries = new ArrayList<>();

        for (Order order : activeOrders) {
            Courier assigned = assignment.findAvailableCourier(order);
            if (assigned == null) {
                System.out.println("  No available courier for order " + order.getId());
                continue;
            }

            System.out.println("  Order " + order.getId() + " → Courier " + assigned.getName() + " (" + assigned.getId() + ")");

            // Mark courier busy
            assignment.updateStatus(assigned, CourierStatus.BUSY);

            // -----------------------------------------------------------
            // ALPHA 4 — Route Navigation (BFS + Dijkstra)
            // -----------------------------------------------------------
            try {
                RouteNavigationService.RouteResult route = routeNav.getRoute(order, assigned);

                // Estimate time (simple: 15 min prep + distance * 2 min/km)
                int estTime = 15 + (int) Math.ceil(route.getTotalDistance() * 2);

                Delivery delivery = new Delivery(
                        "DEL-" + order.getId(),
                        order,
                        route.getTotalDistance(),
                        estTime
                );
                delivery.setCourier(assigned);

                completedDeliveries.add(delivery);

                System.out.println("    Route: " + route.getPath().size() + " hops, "
                        + String.format("%.2f km", route.getTotalDistance())
                        + ", ~" + estTime + " min");

                // Mark courier available again for next order (demo simplification)
                assignment.updateStatus(assigned, CourierStatus.AVAILABLE);

            } catch (IllegalStateException e) {
                System.out.println("    Routing failed: " + e.getMessage());
                assignment.updateStatus(assigned, CourierStatus.AVAILABLE);
            }
        }
        System.out.println();

        // ---------------------------------------------------------------
        // ALPHA 5 — Optimization & Reporting
        // ---------------------------------------------------------------
        System.out.println("[Alpha 5] Generating delivery optimization report...");
        DeliveryOptimizationService optimizer = new DeliveryOptimizationService();
        String report = optimizer.generateReport(completedDeliveries);
        System.out.println(report);

        // Also show MST (network optimization)
        System.out.println("\n[Alpha 4] Minimum Spanning Network (Kruskal):");
        var mst = routeNav.minimumSpanningNetwork();
        System.out.println("  MST edges: " + mst.getEdges().size()
                + ", total weight: " + String.format("%.2f km", mst.getTotalWeight()));

        System.out.println("\n=== Pipeline complete ===");
    }

    // ---- Seed data loaders (mirroring CSV structure) ----

    private static List<Customer> loadCustomers(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Customer> list = new ArrayList<>();
        Path path = Paths.get("data/seed/customers.csv");
        java.nio.file.Files.lines(path).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 4) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            int locId = Integer.parseInt(p[3].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Customer(id, name, phone, loc));
        });
        return list;
    }

    private static List<Restaurant> loadRestaurants(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Restaurant> list = new ArrayList<>();
        Path path = Paths.get("data/seed/restaurants.csv");
        java.nio.file.Files.lines(path).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 4) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            int locId = Integer.parseInt(p[3].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Restaurant(id, name, loc));
        });
        return list;
    }

    private static List<Courier> loadCouriers(java.util.Map<Integer, String> locationNames) throws IOException {
        List<Courier> list = new ArrayList<>();
        Path path = Paths.get("data/seed/couriers.csv");
        java.nio.file.Files.lines(path).skip(1).forEach(line -> {
            if (line.isBlank()) return;
            String[] p = line.split(",");
            if (p.length < 5) return;
            String id = p[0].trim();
            String name = p[1].trim();
            String phone = p[2].trim();
            CourierStatus status = CourierStatus.valueOf(p[3].trim().toUpperCase());
            int locId = Integer.parseInt(p[4].trim());
            String locName = locationNames.getOrDefault(locId, "Unknown");
            Location loc = new Location(String.valueOf(locId), locName, 0, 0);
            list.add(new Courier(id, name, phone, status, loc));
        });
        return list;
    }
}