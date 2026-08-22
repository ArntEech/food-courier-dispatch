package com.foodcourier.alpha4;

import com.foodcourier.algorithms.graph.BFS;
import com.foodcourier.algorithms.graph.Dijkstra;
import com.foodcourier.algorithms.graph.Kruskal;
import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.domain.Courier;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Alpha 4 — Route & Navigation. Wires together the graph, BFS, Dijkstra,
 * and Kruskal into the single method Alpha 3 and Alpha 5 actually depend
 * on: getRoute(Order, Courier).
 *
 * STATUS: complete. All dependencies (Graph<V>, BFS, Dijkstra, Kruskal,
 * DisjointSet) are real and verified against real seed data.
 *
 * Design note: works internally in Integer location IDs (matching
 * DataLoader's Graph<Integer>, the pipeline that's actually tested end
 * to end) rather than Graph<Location>. Location -> Integer translation
 * happens at the boundary via Location.getId(), parsed as an int (the
 * seed CSVs use numeric location_id, e.g. "1", "2" — Location.getId()
 * holds that same string).
 *
 * Owner: Jonathan (integration). Built on:
 *  - Caleb   -> dsa/graph/Graph.java (canonical), DataLoader
 *  - Frank   -> algorithms/graph/BFS.java, DFS.java
 *  - Daniel  -> algorithms/graph/Dijkstra.java, Prim.java
 *  - Jonathan (me) -> algorithms/graph/Kruskal.java, this class
 */
public class RouteNavigationService {

    private final Graph<Integer> graph;
    private final DisjointSetInterface<Integer> disjointSet;

    public RouteNavigationService(Graph<Integer> graph,
                                   DisjointSetInterface<Integer> disjointSet) {
        this.graph = graph;
        this.disjointSet = disjointSet;
    }

    /**
     * Main entry point — what Alpha 3 calls and what Alpha 5 consumes.
     *
     * Route = courier's current location -> restaurant -> customer,
     * summed as one route with total distance.
     *
     * @throws IllegalStateException if no route exists between the
     *         required locations (checked via BFS before attempting
     *         Dijkstra, so the failure is clear rather than Dijkstra
     *         silently returning an empty path).
     */
    public RouteResult getRoute(Order order, Courier courier) {
        int start = locationId(courier.getCurrentLocation());
        int pickup = locationId(order.getRestaurant().getLocation());
        int dropoff = locationId(order.getCustomer().getLocation());

        if (!BFS.isReachable(graph, start, pickup)) {
            throw new IllegalStateException(
                    "No route exists from courier's location to the restaurant.");
        }
        if (!BFS.isReachable(graph, pickup, dropoff)) {
            throw new IllegalStateException(
                    "No route exists from the restaurant to the customer.");
        }

        // --- Leg 1: courier -> restaurant ---
        List<Integer> leg1Path = Dijkstra.shortestPath(graph, start, pickup);
        double leg1Distance = Dijkstra.shortestDistance(graph, start, pickup);

        // --- Leg 2: restaurant -> customer ---
        List<Integer> leg2Path = Dijkstra.shortestPath(graph, pickup, dropoff);
        double leg2Distance = Dijkstra.shortestDistance(graph, pickup, dropoff);

        List<Integer> fullPath = combinePaths(leg1Path, leg2Path);
        double totalDistance = leg1Distance + leg2Distance;

        return new RouteResult(fullPath, totalDistance);
    }

    /**
     * Connectivity check — used before routing, or standalone by Alpha 5
     * to sanity-check the network. Backed by Frank's real BFS.
     */
    public boolean isReachable(Location from, Location to) {
        return BFS.isReachable(graph, locationId(from), locationId(to));
    }

    /**
     * Network-wide minimum spanning tree — "what's the minimal set of roads
     * needed to keep every location connected?" Backed by Kruskal.
     */
    public Kruskal.Result<Integer> minimumSpanningNetwork() {
        return Kruskal.minimumSpanningTree(graph, disjointSet);
    }

    // ---- helpers ----

    private int locationId(Location location) {
        return Integer.parseInt(location.getId());
    }

    private List<Integer> combinePaths(List<Integer> leg1, List<Integer> leg2) {
        List<Integer> combined = new ArrayList<>(leg1);
        // avoid duplicating the shared midpoint (restaurant) — both legs include it
        combined.addAll(leg2.subList(1, leg2.size()));
        return combined;
    }

    /** What getRoute() hands back to Alpha 5. */
    public static class RouteResult {
        private final List<Integer> path;
        private final double totalDistance;

        public RouteResult(List<Integer> path, double totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }

        public List<Integer> getPath() { return path; }
        public double getTotalDistance() { return totalDistance; }
    }
}