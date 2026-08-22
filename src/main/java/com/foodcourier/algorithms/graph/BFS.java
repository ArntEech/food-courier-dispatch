package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.graph.Graph;

import java.util.NoSuchElementException;

/**
 * Breadth-First Search over the Alpha 4 navigation {@link Graph}.
 *
 * Used to explore the road network level-by-level, e.g. to find the
 * fewest-hops route between two locations, or to confirm that every
 * location in the network is reachable (connectivity check).
 *
 * Implemented with a small hand-rolled circular array queue rather than
 * java.util.Queue, consistent with the project's "custom data structure"
 * requirement for assessed components.
 *
 * Time complexity:  O(V + E)
 * Space complexity: O(V)
 */
public final class BFS {

    private BFS() {
        // static utility class
    }

    /** Minimal growable circular-array queue used purely to drive the BFS frontier. */
    private static final class IntQueue {
        private int[] data;
        private int head;
        private int tail;
        private int count;

        IntQueue(int capacity) {
            data = new int[Math.max(capacity, 4)];
            head = 0;
            tail = 0;
            count = 0;
        }

        boolean isEmpty() {
            return count == 0;
        }

        void enqueue(int value) {
            if (count == data.length) {
                grow();
            }
            data[tail] = value;
            tail = (tail + 1) % data.length;
            count++;
        }

        int dequeue() {
            if (isEmpty()) {
                throw new NoSuchElementException("Queue is empty");
            }
            int value = data[head];
            head = (head + 1) % data.length;
            count--;
            return value;
        }

        private void grow() {
            int[] newData = new int[data.length * 2];
            for (int i = 0; i < count; i++) {
                newData[i] = data[(head + i) % data.length];
            }
            data = newData;
            head = 0;
            tail = count;
        }
    }

    /**
     * Traverses the graph breadth-first starting from {@code startId}.
     *
     * @return the visited vertex ids, in the order they were discovered.
     */
    public static String[] traverse(Graph graph, String startId) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        if (!graph.containsVertex(startId)) {
            throw new NoSuchElementException("Unknown start vertex: " + startId);
        }

        int n = graph.size();
        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderSize = 0;

        IntQueue queue = new IntQueue(n);
        int startIndex = graph.indexOf(startId);
        visited[startIndex] = true;
        queue.enqueue(startIndex);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            order[orderSize++] = current;

            Graph.Edge edge = graph.neighborsOf(current);
            while (edge != null) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    queue.enqueue(edge.to);
                }
                edge = edge.next;
            }
        }

        String[] result = new String[orderSize];
        for (int i = 0; i < orderSize; i++) {
            result[i] = graph.idOf(order[i]);
        }
        return result;
    }

    /**
     * Returns the length, in edges, of the shortest path (fewest hops) between
     * {@code startId} and {@code targetId}, or -1 if no path exists.
     */
    public static int shortestHopDistance(Graph graph, String startId, String targetId) {
        if (!graph.containsVertex(startId) || !graph.containsVertex(targetId)) {
            throw new NoSuchElementException("Unknown vertex in graph");
        }

        int n = graph.size();
        boolean[] visited = new boolean[n];
        int[] distance = new int[n];
        IntQueue queue = new IntQueue(n);

        int startIndex = graph.indexOf(startId);
        int targetIndex = graph.indexOf(targetId);

        visited[startIndex] = true;
        distance[startIndex] = 0;
        queue.enqueue(startIndex);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            if (current == targetIndex) {
                return distance[current];
            }
            Graph.Edge edge = graph.neighborsOf(current);
            while (edge != null) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    distance[edge.to] = distance[current] + 1;
                    queue.enqueue(edge.to);
                }
                edge = edge.next;
            }
        }
        return -1;
    }

    /**
     * Confirms whether the entire network is connected, i.e. whether every
     * vertex is reachable from an arbitrary starting vertex. An empty graph
     * is trivially connected; a single-vertex graph is always connected.
     */
    public static boolean isConnected(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        if (graph.size() <= 1) {
            return true;
        }
        String startId = graph.vertexIds()[0];
        String[] reached = traverse(graph, startId);
        return reached.length == graph.size();
    }
}
