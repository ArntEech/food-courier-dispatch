package com.foodcourier.dsa.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph<V> implements GraphInterface<V> {

    private final Map<V, List<Edge<V>>> adjacencyMap;
    private int edgeCount;

    public Graph() {
        this.adjacencyMap = new LinkedHashMap<>();
        this.edgeCount = 0;
    }

    @Override
    public void addVertex(V vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null.");
        }

        adjacencyMap.computeIfAbsent(vertex, key -> new ArrayList<>());
    }

    @Override
    public void addEdge(V from, V to) {
        addEdge(from, to, 1.0);
    }

    @Override
    public void addEdge(V from, V to, double weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Edge endpoints cannot be null.");
        }

        addVertex(from);
        addVertex(to);
        adjacencyMap.get(from).add(new Edge<>(from, to, weight));
        edgeCount++;
    }

    @Override
    public void addUndirectedEdge(V from, V to) {
        addUndirectedEdge(from, to, 1.0);
    }

    @Override
    public void addUndirectedEdge(V from, V to, double weight) {
        addEdge(from, to, weight);
        addEdge(to, from, weight);
    }

    @Override
    public boolean containsVertex(V vertex) {
        return adjacencyMap.containsKey(vertex);
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        if (!containsVertex(vertex)) {
            return Collections.emptyList();
        }

        List<V> neighbors = new ArrayList<>();
        for (Edge<V> edge : adjacencyMap.get(vertex)) {
            neighbors.add(edge.getTo());
        }
        return neighbors;
    }

    public List<Edge<V>> getEdgesFrom(V vertex) {
        if (!containsVertex(vertex)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(adjacencyMap.get(vertex));
    }

    public Set<V> getVertices() {
        return new LinkedHashSet<>(adjacencyMap.keySet());
    }

    public Map<V, List<Edge<V>>> getAdjacencyMap() {
        Map<V, List<Edge<V>>> copy = new LinkedHashMap<>();
        for (Map.Entry<V, List<Edge<V>>> entry : adjacencyMap.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    @Override
    public int vertexCount() {
        return adjacencyMap.size();
    }

    @Override
    public int edgeCount() {
        return edgeCount;
    }
}
