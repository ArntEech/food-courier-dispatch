package com.foodcourier.algorithms.graph;

/**
 * Lightweight weighted-edge record.
 *
 * TEMPORARY NOTE: GraphInterface currently has no way to list edges with
 * their weights (getNeighbors() only returns List<V>). Kruskal needs the
 * full weighted edge list up front, so this class exists as a workaround
 * until GraphInterface exposes something like getEdges() or getWeight(from, to).
 * Flag this to Caleb — ideally this becomes unnecessary once that's added,
 * or this class becomes the canonical Edge type the interface itself returns.
 */
public class Edge<V> {

    public final V from;
    public final V to;
    public final double weight;

    public Edge(V from, V to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return from + " -- " + to + " (" + weight + ")";
    }
}
