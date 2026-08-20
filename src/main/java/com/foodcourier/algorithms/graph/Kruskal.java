package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Kruskal's algorithm — builds a Minimum Spanning Tree (MST) over a
 * weighted graph, using a Disjoint Set (union-find) to detect cycles.
 *
 * Written as a static utility class to match Dijkstra's and Prim's style
 * for consistency across Alpha 4's algorithms.
 *
 * Used here to answer a network-design question — "what's the minimal
 * set of roads needed to keep every location connected?" — distinct from
 * Dijkstra's single-route shortest-path question, and an independent
 * cross-check against Prim (both should produce the same total weight
 * for a connected graph, since MST weight is unique even when the exact
 * edge set may not be).
 *
 * Complexity: O(E log E), dominated by the sort; union-find operations
 * are near O(1) amortised with path compression + union by rank.
 */
public final class Kruskal {

    private Kruskal() {
    }

    /**
     * @param graph       the graph to build the MST over
     * @param disjointSet a DisjointSetInterface implementation
     * @return a Result containing the MST edges and total weight
     */
    public static <V> Result<V> minimumSpanningTree(Graph<V> graph, DisjointSetInterface<V> disjointSet) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }

        // 1. every vertex starts in its own set
        for (V v : graph.getVertices()) {
            disjointSet.makeSet(v);
        }

        // 2. collect all edges from the graph
        //    NOTE: for undirected edges, addUndirectedEdge() stores both
        //    directions (A->B and B->A) as separate Edge objects, so this
        //    list will contain both. Harmless for Kruskal — union-find
        //    naturally skips the redundant direction once the first one
        //    connects the pair — but worth knowing if edge counts look
        //    roughly double what you'd expect from the raw road count.
        List<Edge<V>> allEdges = new ArrayList<>();
        for (V vertex : graph.getVertices()) {
            allEdges.addAll(graph.getEdgesFrom(vertex));
        }

        // 3. sort edges by weight ascending (merge sort on a List — no
        //    generic array creation, no unchecked-conversion warnings)
        List<Edge<V>> sorted = mergeSort(allEdges);

        // 4. greedily add edges that don't form a cycle
        List<Edge<V>> mst = new ArrayList<>();
        for (Edge<V> e : sorted) {
            if (!disjointSet.connected(e.getFrom(), e.getTo())) {
                disjointSet.union(e.getFrom(), e.getTo());
                mst.add(e);
            }
        }

        return new Result<>(mst, graph.vertexCount());
    }

    // ---- merge sort on List<Edge<V>>, by weight ----

    private static <V> List<Edge<V>> mergeSort(List<Edge<V>> input) {
        if (input.size() <= 1) {
            return input;
        }
        int mid = input.size() / 2;
        List<Edge<V>> left = mergeSort(new ArrayList<>(input.subList(0, mid)));
        List<Edge<V>> right = mergeSort(new ArrayList<>(input.subList(mid, input.size())));
        return merge(left, right);
    }

    private static <V> List<Edge<V>> merge(List<Edge<V>> left, List<Edge<V>> right) {
        List<Edge<V>> result = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getWeight() <= right.get(j).getWeight()) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    /** Mirrors Prim.Result's shape for consistency between the two MST algorithms. */
    public static final class Result<V> {

        private final List<Edge<V>> edges;
        private final int vertexCount;

        private Result(List<Edge<V>> edges, int vertexCount) {
            this.edges = List.copyOf(edges);
            this.vertexCount = vertexCount;
        }

        public List<Edge<V>> getEdges() {
            return edges;
        }

        public double getTotalWeight() {
            return edges.stream().mapToDouble(Edge::getWeight).sum();
        }

        public boolean connectsAllVertices() {
            return edges.size() == vertexCount - 1;
        }
    }
}
