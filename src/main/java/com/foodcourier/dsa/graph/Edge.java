package com.foodcourier.dsa.graph;

public class Edge<V> {

    private final V from;
    private final V to;
    private final double weight;

    public Edge(V from, V to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public V getFrom() {
        return from;
    }

    public V getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " (" + weight + ")";
    }
}
