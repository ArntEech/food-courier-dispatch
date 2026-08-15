package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.disjointset.DisjointSetInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Kruskal's algorithm — builds a Minimum Spanning Tree (MST) over a set of
 * weighted edges, using a Disjoint Set (union-find) to detect cycles.
 *
 * Used in this project to analyse the road network (e.g. "what's the minimal
 * set of roads needed to keep every location connected?") — a network-design
 * question, distinct from Dijkstra's single-route shortest-path question.
 *
 * Complexity: O(E log E) dominated by the sort; union-find operations are
 * near O(1) amortised with path compression + union by rank/size.
 */
public class Kruskal<V> {

    /**
     * @param vertices  every vertex in the graph (each gets its own set first)
     * @param edges     every edge in the graph, unsorted
     * @param disjointSet a DisjointSetInterface implementation (Caleb's real
     *                    one once available; this class does not care which)
     * @return the list of edges that form the MST
     */
    public List<Edge<V>> findMST(List<V> vertices,
                                  List<Edge<V>> edges,
                                  DisjointSetInterface<V> disjointSet) {

        // 1. every vertex starts in its own set
        for (V v : vertices) {
            disjointSet.makeSet(v);
        }

        // 2. sort edges by weight ascending (merge sort — no built-in Collections.sort)
        Edge<V>[] sorted = edges.toArray(new Edge[0]);
        mergeSort(sorted, 0, sorted.length - 1);

        // 3. greedily add edges that don't form a cycle
        List<Edge<V>> mst = new ArrayList<>();
        for (Edge<V> e : sorted) {
            if (!disjointSet.connected(e.from, e.to)) {
                disjointSet.union(e.from, e.to);
                mst.add(e);
            }
        }
        return mst;
    }

    // ---- merge sort on Edge[], by weight ----

    private void mergeSort(Edge<V>[] arr, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) / 2;
        mergeSort(arr, lo, mid);
        mergeSort(arr, mid + 1, hi);
        merge(arr, lo, mid, hi);
    }

    @SuppressWarnings("unchecked")
    private void merge(Edge<V>[] arr, int lo, int mid, int hi) {
        Edge<V>[] left = java.util.Arrays.copyOfRange(arr, lo, mid + 1);
        Edge<V>[] right = java.util.Arrays.copyOfRange(arr, mid + 1, hi + 1);

        int i = 0, j = 0, k = lo;
        while (i < left.length && j < right.length) {
            arr[k++] = (left[i].weight <= right[j].weight) ? left[i++] : right[j++];
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }
}
