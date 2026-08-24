package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

import java.util.ArrayList;
import java.util.List;
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
 *
 * NOTE: uses java.util.HashSet for visited-tracking, since Graph<V> is
 * generic (no index mapping to drive an array-based visited[] the way an
 * int-ID graph would). Matches the same pattern already used in
 * Dijkstra.java (java.util.Map) and BFS.java in this package.
 */
public final class DFS {

    private DFS() {
        // static utility class
    }

    /** Minimal growable generic array stack used purely to drive the DFS frontier. */
    private static final class GenericStack<T> {
        private Object[] data;
        private int top;

        @SuppressWarnings("unchecked")
        GenericStack(int capacity) {
            data = new Object[Math.max(capacity, 4)];
            top = 0;
        }

        boolean isEmpty() {
            return top == 0;
        }

        void push(T value) {
            if (top == data.length) {
                grow();
            }
            data[top++] = value;
        }

        @SuppressWarnings("unchecked")
        T pop() {
            if (isEmpty()) {
                throw new NoSuchElementException("Stack is empty");
            }
            return (T) data[--top];
        }

        private void grow() {
            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, top);
            data = newData;
        }
    }

    /**
     * Traverses the graph depth-first starting from {@code start}.
     *
     * @return the visited vertices, in the order they were first discovered.
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

        GenericStack<V> stack = new GenericStack<>(graph.vertexCount());
        stack.push(start);

        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            visitedOrder.add(current);

            // Push neighbours; since this is a stack, pushing in adjacency-list
            // order and popping reverses it. That is fine for a valid DFS order
            // (any order that respects "go deep before backtracking" is valid).
            for (Edge<V> edge : graph.getEdgesFrom(current)) {
                V neighbor = edge.getTo();
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }

        return visitedOrder;
    }

    /** Returns true if {@code target} is reachable from {@code start}. */
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