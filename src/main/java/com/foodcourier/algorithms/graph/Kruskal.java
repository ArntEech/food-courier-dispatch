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
     * @param vertices    every vertex in the graph (each gets its own set first)
     * @param edges       every edge in the graph, unsorted
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

        // 2. sort edges by weight ascending (merge sort on a List — no
        // generic array creation, so no unchecked-conversion warning)
        List<Edge<V>> sorted = mergeSort(edges);

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

    // ---- merge sort on List<Edge<V>>, by weight ----

    private List<Edge<V>> mergeSort(List<Edge<V>> input) {
        if (input.size() <= 1) {
            return input;
        }
        int mid = input.size() / 2;
        List<Edge<V>> left = mergeSort(new ArrayList<>(input.subList(0, mid)));
        List<Edge<V>> right = mergeSort(new ArrayList<>(input.subList(mid, input.size())));
        return merge(left, right);
    }

    private List<Edge<V>> merge(List<Edge<V>> left, List<Edge<V>> right) {
        List<Edge<V>> result = new ArrayList<>(left.size() + right.size());
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).weight <= right.get(j).weight) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size())
            result.add(left.get(i++));
        while (j < right.size())
            result.add(right.get(j++));
        return result;
    }
}