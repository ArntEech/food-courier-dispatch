package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.graph.Graph;

import java.util.NoSuchElementException;

/**
 * Depth-First Search over the Alpha 4 navigation {@link Graph}.
 *
 * Used for tasks such as cycle detection support, exploring all routes
 * reachable from a hub, and (like {@link BFS}) confirming network
 * connectivity.
 *
 * Implemented iteratively with a small hand-rolled array-based stack rather
 * than java.util.Deque/Stack or language recursion, so it will not blow the
 * call stack on large road networks and stays consistent with the project's
 * "custom data structure" requirement for assessed components.
 *
 * Time complexity:  O(V + E)
 * Space complexity: O(V)
 */
public final class DFS {

    private DFS() {
        // static utility class
    }

    /** Minimal growable array stack used purely to drive the DFS frontier. */
    private static final class IntStack {
        private int[] data;
        private int top;

        IntStack(int capacity) {
            data = new int[Math.max(capacity, 4)];
            top = 0;
        }

        boolean isEmpty() {
            return top == 0;
        }

        void push(int value) {
            if (top == data.length) {
                grow();
            }
            data[top++] = value;
        }

        int pop() {
            if (isEmpty()) {
                throw new NoSuchElementException("Stack is empty");
            }
            return data[--top];
        }

        private void grow() {
            int[] newData = new int[data.length * 2];
            System.arraycopy(data, 0, newData, 0, top);
            data = newData;
        }
    }

    /**
     * Traverses the graph depth-first starting from {@code startId}.
     *
     * @return the visited vertex ids, in the order they were first discovered.
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

        IntStack stack = new IntStack(n);
        stack.push(graph.indexOf(startId));

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (visited[current]) {
                continue;
            }
            visited[current] = true;
            order[orderSize++] = current;

            // Push neighbours; since this is a stack, pushing in adjacency-list
            // order and popping reverses it. That is fine for a valid DFS order
            // (any order that respects "go deep before backtracking" is valid).
            Graph.Edge edge = graph.neighborsOf(current);
            while (edge != null) {
                if (!visited[edge.to]) {
                    stack.push(edge.to);
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

    /** Returns true if {@code targetId} is reachable from {@code startId}. */
    public static boolean isReachable(Graph graph, String startId, String targetId) {
        if (!graph.containsVertex(targetId)) {
            throw new NoSuchElementException("Unknown vertex: " + targetId);
        }
        for (String visited : traverse(graph, startId)) {
            if (visited.equals(targetId)) {
                return true;
            }
        }
        return false;
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
