package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Breadth-First Search over the Alpha 4 navigation {@link Graph}.
 *
 * Used to explore the road network level-by-level, e.g. to find the
 * fewest-hops route between two locations, or to confirm that every
 * location in the network is reachable (connectivity check).
 *
 * Implemented with a small hand-rolled linked-node queue rather than
 * java.util.Queue, consistent with the project's "custom data structure"
 * requirement for assessed components.
 *
 * Time complexity:  O(V + E)
 * Space complexity: O(V)
 *
 * NOTE: uses java.util.HashSet/HashMap for visited-tracking, since Graph<V>
 * is generic (no index mapping to drive array-based visited[] the way an
 * int-ID graph would). This matches the same pattern Dijkstra.java already
 * uses (java.util.Map for its distance table) — worth flagging to the team
 * if a stricter "no built-in collections" reading is wanted, since it'd
 * mean revisiting Dijkstra too, not just this file.
 */
public final class BFS {

    private BFS() {
        // static utility class
    }

    /** Minimal singly-linked queue used purely to drive the BFS frontier. */
    private static final class NodeQueue<T> {
        private static final class Node<T> {
            final T value;
            Node<T> next;
            Node(T value) { this.value = value; }
        }

        private Node<T> head;
        private Node<T> tail;

        boolean isEmpty() {
            return head == null;
        }

        void enqueue(T value) {
            Node<T> node = new Node<>(value);
            if (tail == null) {
                head = node;
                tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }

        T dequeue() {
            if (isEmpty()) {
                throw new NoSuchElementException("Queue is empty");
            }
            T value = head.value;
            head = head.next;
            if (head == null) {
                tail = null;
            }
            return value;
        }
    }

    /**
     * Traverses the graph breadth-first starting from {@code start}.
     *
     * @return the visited vertices, in the order they were discovered.
     */
    public static <V> List<V> traverse(Graph<V> graph, V start) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        if (!graph.containsVertex(start)) {
            throw new NoSuchElementException("Unknown start vertex: " + start);
        }

        List<V> visitedOrder = new ArrayList<>();
        java.util.Set<V> visited = new java.util.HashSet<>();

        NodeQueue<V> queue = new NodeQueue<>();
        visited.add(start);
        queue.enqueue(start);

        while (!queue.isEmpty()) {
            V current = queue.dequeue();
            visitedOrder.add(current);

            for (Edge<V> edge : graph.getEdgesFrom(current)) {
                V neighbor = edge.getTo();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.enqueue(neighbor);
                }
            }
        }

        return visitedOrder;
    }

    /**
     * Returns the length, in edges, of the shortest path (fewest hops) between
     * {@code start} and {@code target}, or -1 if no path exists.
     */
    public static <V> int shortestHopDistance(Graph<V> graph, V start, V target) {
        if (!graph.containsVertex(start) || !graph.containsVertex(target)) {
            throw new NoSuchElementException("Unknown vertex in graph");
        }

        java.util.Map<V, Integer> distance = new java.util.HashMap<>();
        java.util.Set<V> visited = new java.util.HashSet<>();
        NodeQueue<V> queue = new NodeQueue<>();

        visited.add(start);
        distance.put(start, 0);
        queue.enqueue(start);

        while (!queue.isEmpty()) {
            V current = queue.dequeue();
            if (current.equals(target)) {
                return distance.get(current);
            }
            for (Edge<V> edge : graph.getEdgesFrom(current)) {
                V neighbor = edge.getTo();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distance.put(neighbor, distance.get(current) + 1);
                    queue.enqueue(neighbor);
                }
            }
        }
        return -1;
    }

    /**
     * True if {@code target} is reachable from {@code start}. Used by
     * RouteNavigationService as a pre-check before attempting to route.
     */
    public static <V> boolean isReachable(Graph<V> graph, V start, V target) {
        if (!graph.containsVertex(target)) {
            throw new NoSuchElementException("Unknown vertex: " + target);
        }
        return traverse(graph, start).contains(target);
    }

    /**
     * Confirms whether the entire network is connected, i.e. whether every
     * vertex is reachable from an arbitrary starting vertex. An empty graph
     * is trivially connected; a single-vertex graph is always connected.
     */
    public static <V> boolean isConnected(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        if (graph.vertexCount() <= 1) {
            return true;
        }
        V start = graph.getVertices().iterator().next();
        List<V> reached = traverse(graph, start);
        return reached.size() == graph.vertexCount();
    }
}