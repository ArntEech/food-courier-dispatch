package com.foodcourier.alpha4;

import com.foodcourier.algorithms.graph.Edge;
import com.foodcourier.algorithms.graph.Kruskal;
import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import com.foodcourier.dsa.graph.GraphInterface;
import com.foodcourier.domain.Courier;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;

import java.util.List;

/**
 * Alpha 4 — Route & Navigation. Wires together the graph, BFS/DFS,
 * Dijkstra, Prim, and Kruskal into the single method Alpha 3 and Alpha 5
 * actually depend on: getRoute(Order, Courier).
 *
 * STATUS: sketch only. This class compiles once the teammate pieces below
 * exist with the signatures listed. Everything using a real teammate class
 * is marked TODO with the expected contract — swap in the real call once
 * it's pushed.
 *
 * Owner: Jonathan (integration). Depends on:
 *  - Caleb   -> dsa/graph/AdjacencyListGraph.java, dsa/disjointset/DisjointSet.java
 *  - Frank   -> algorithms/graph/BFS.java, DFS.java
 *  - Daniel  -> algorithms/graph/Dijkstra.java, Prim.java
 *  - Jonathan (me) -> algorithms/graph/Kruskal.java (done)
 */
public class RouteNavigationService {

    private final GraphInterface<Location> graph;
    private final DisjointSetInterface<Location> disjointSet;

    public RouteNavigationService(GraphInterface<Location> graph,
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
        // CONTRACT expected from Daniel's Dijkstra.java:
        //   public static <V> PathResult<V> shortestPath(GraphInterface<V> graph, V from, V to)
        //   where PathResult<V> has: List<V> getPath(); double getTotalWeight();
        //
        // PathResult<Location> leg1 = Dijkstra.shortestPath(graph, start, pickup);
        PathResult<Location> leg1 = requireDijkstra(start, pickup); // TODO: replace once Daniel pushes Dijkstra

        // --- Leg 2: restaurant -> customer ---
        // PathResult<Location> leg2 = Dijkstra.shortestPath(graph, pickup, dropoff);
        PathResult<Location> leg2 = requireDijkstra(pickup, dropoff); // TODO: replace once Daniel pushes Dijkstra

        List<Location> fullPath = combinePaths(leg1, leg2);
        double totalDistance = leg1.getTotalWeight() + leg2.getTotalWeight();

        return new RouteResult(fullPath, totalDistance);
    }

    /**
     * Connectivity check — used before routing, or standalone by Alpha 5
     * to sanity-check the network. Uses BFS once Frank pushes it.
     *
     * CONTRACT expected from Frank's BFS.java:
     *   public static <V> boolean isReachable(GraphInterface<V> graph, V from, V to)
     */
    public boolean isReachable(Location from, Location to) {
        // return BFS.isReachable(graph, from, to);
        throw new UnsupportedOperationException("waiting on Frank's BFS.java");
    }

    /**
     * Network-wide minimum spanning tree — "what's the minimal set of roads
     * needed to keep every location connected?" Uses Kruskal (mine, already
     * implemented) plus the vertex/edge lists pulled from the real graph.
     *
     * CONTRACT still needed from Caleb: a way to list all edges with
     * weights from GraphInterface (it currently only exposes
     * getNeighbors() -> List<V>, no weights). Flagged separately.
     */
    public List<Edge<Location>> minimumSpanningNetwork(List<Location> vertices,
                                                          List<Edge<Location>> allEdges) {
        Kruskal<Location> kruskal = new Kruskal<>();
        return kruskal.findMST(vertices, allEdges, disjointSet);
    }

    // ---- helpers ----

    private PathResult<Location> requireDijkstra(Location from, Location to) {
        throw new UnsupportedOperationException(
                "waiting on Daniel's Dijkstra.java — expected signature: " +
                "PathResult<V> shortestPath(GraphInterface<V> graph, V from, V to)");
    }

    private List<Location> combinePaths(PathResult<Location> leg1, PathResult<Location> leg2) {
        List<Location> combined = new java.util.ArrayList<>(leg1.getPath());
        // avoid duplicating the shared midpoint (restaurant) if both legs include it
        List<Location> leg2Path = leg2.getPath();
        combined.addAll(leg2Path.subList(1, leg2Path.size()));
        return combined;
    }

    /**
     * Minimal placeholder for whatever shape Daniel's Dijkstra actually
     * returns — align this with his real return type once pushed, don't
     * assume this exact class survives unchanged.
     */
    public interface PathResult<V> {
        List<V> getPath();
        double getTotalWeight();
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
