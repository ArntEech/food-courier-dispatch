package com.foodcourier.alpha4;

import com.foodcourier.algorithms.graph.Kruskal;
import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.domain.Courier;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;

import java.util.List;

/**
 * Alpha 4 — Route & Navigation. Wires together the graph, BFS/DFS,
 * Dijkstra, Prim, and Kruskal into the single method Alpha 3 and Alpha 5
 * actually depend on: getRoute(Order, Courier).
 *
 * STATUS: sketch, updated to match the canonical dsa.graph.Graph<V> /
 * dsa.graph.Edge<V> after standardizing away from the old
 * AdjacencyListGraph + algorithms.graph.Edge. Still compiles against
 * placeholders for BFS/Dijkstra — swap those in once Frank/Daniel's
 * real classes are wired in here instead of the graph-agnostic stubs.
 *
 * Owner: Jonathan (integration). Depends on:
 *  - Caleb   -> dsa/graph/Graph.java (now canonical), data loading
 *  - Frank   -> algorithms/graph/BFS.java, DFS.java
 *  - Daniel  -> algorithms/graph/Dijkstra.java, Prim.java (done)
 *  - Jonathan (me) -> algorithms/graph/Kruskal.java (done)
 */
public class RouteNavigationService {

    private final Graph<Location> graph;
    private final DisjointSetInterface<Location> disjointSet;

    public RouteNavigationService(Graph<Location> graph,
                                   DisjointSetInterface<Location> disjointSet) {
        this.graph = graph;
        this.disjointSet = disjointSet;
    }

    /**
     * Main entry point — what Alpha 3 calls and what Alpha 5 consumes.
     *
     * Route = courier's current location -> restaurant -> customer,
     * summed as one route with total distance/time.
     */
    public RouteResult getRoute(Order order, Courier courier) {
        Location start = courier.getCurrentLocation();
        Location pickup = order.getRestaurant().getLocation();
        Location dropoff = order.getCustomer().getLocation();

        // --- Leg 1: courier -> restaurant ---
        // CONTRACT expected from Daniel's Dijkstra.java (already real):
        //   Dijkstra.shortestPath(Graph<V> graph, V source, V target) -> List<V>
        //   Dijkstra.shortestDistance(Graph<V> graph, V source, V target) -> double
        List<Location> leg1Path = requireDijkstraPath(start, pickup); // TODO: wire to Dijkstra.shortestPath(graph, start, pickup)
        double leg1Distance = requireDijkstraDistance(start, pickup); // TODO: wire to Dijkstra.shortestDistance(graph, start, pickup)

        // --- Leg 2: restaurant -> customer ---
        List<Location> leg2Path = requireDijkstraPath(pickup, dropoff); // TODO: wire to Dijkstra.shortestPath(graph, pickup, dropoff)
        double leg2Distance = requireDijkstraDistance(pickup, dropoff); // TODO: wire to Dijkstra.shortestDistance(graph, pickup, dropoff)

        List<Location> fullPath = combinePaths(leg1Path, leg2Path);
        double totalDistance = leg1Distance + leg2Distance;

        return new RouteResult(fullPath, totalDistance);
    }

    /**
     * Connectivity check — used before routing, or standalone by Alpha 5
     * to sanity-check the network. Uses BFS once Frank pushes it.
     *
     * CONTRACT expected from Frank's BFS.java:
     *   public static <V> boolean isReachable(Graph<V> graph, V from, V to)
     */
    public boolean isReachable(Location from, Location to) {
        // return BFS.isReachable(graph, from, to);
        throw new UnsupportedOperationException("waiting on Frank's BFS.java");
    }

    /**
     * Network-wide minimum spanning tree — "what's the minimal set of roads
     * needed to keep every location connected?" Uses Kruskal (mine, real
     * and already verified against real seed data).
     */
    public List<Edge<Location>> minimumSpanningNetwork() {
        Kruskal.Result<Location> result = Kruskal.minimumSpanningTree(graph, disjointSet);
        return result.getEdges();
    }

    // ---- helpers ----

    private List<Location> requireDijkstraPath(Location from, Location to) {
        throw new UnsupportedOperationException(
                "wire to Dijkstra.shortestPath(graph, from, to) — Daniel's Dijkstra is real, just not plugged in here yet");
    }

    private double requireDijkstraDistance(Location from, Location to) {
        throw new UnsupportedOperationException(
                "wire to Dijkstra.shortestDistance(graph, from, to) — Daniel's Dijkstra is real, just not plugged in here yet");
    }

    private List<Location> combinePaths(List<Location> leg1, List<Location> leg2) {
        List<Location> combined = new java.util.ArrayList<>(leg1);
        // avoid duplicating the shared midpoint (restaurant) if both legs include it
        combined.addAll(leg2.subList(1, leg2.size()));
        return combined;
    }

    /** What getRoute() hands back to Alpha 5. */
    public static class RouteResult {
        private final List<Location> path;
        private final double totalDistance;

        public RouteResult(List<Location> path, double totalDistance) {
            this.path = path;
            this.totalDistance = totalDistance;
        }

        public List<Location> getPath() { return path; }
        public double getTotalDistance() { return totalDistance; }
    }
}