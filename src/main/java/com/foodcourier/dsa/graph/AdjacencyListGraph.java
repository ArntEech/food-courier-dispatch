package com.foodcourier.dsa.graph;

import java.util.*;

public class AdjacencyListGraph<V> implements GraphInterface<V> {

    // Stores each vertex and its edges
    private Map<V, List<Edge<V>>> adjacencyMap;

    // Stores the number of edges
    private int edgeCount;

    // Represents an edge
    private static class Edge<V> {
        V destination;
        double weight;

        Edge(V destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Constructor
    public AdjacencyListGraph() {
        adjacencyMap = new HashMap<>();
        edgeCount = 0;
    }

    // Add a vertex
    @Override
    public void addVertex(V vertex) {
        if (!adjacencyMap.containsKey(vertex)) {
            adjacencyMap.put(vertex, new ArrayList<>());
        }
    }

    // Add an unweighted edge
    @Override
    public void addEdge(V from, V to) {
        addEdge(from, to, 0.0);
    }

    // Add a weighted edge
    @Override
    public void addEdge(V from, V to, double weight) {
        addVertex(from);
        addVertex(to);

        List<Edge<V>> edges = adjacencyMap.get(from);

        // Update weight if edge exists
        for (Edge<V> edge : edges) {
            if (edge.destination.equals(to)) {
                edge.weight = weight;
                return;
            }
        }

        // Add new edge
        edges.add(new Edge<>(to, weight));
        edgeCount++;
    }

    // Check if a vertex exists
    @Override
    public boolean containsVertex(V vertex) {
        return adjacencyMap.containsKey(vertex);
    }

    // Get neighboring vertices
    @Override
    public List<V> getNeighbors(V vertex) {
        if (!adjacencyMap.containsKey(vertex)) {
            return new ArrayList<>();
        }

        List<Edge<V>> edges = adjacencyMap.get(vertex);
        List<V> neighbors = new ArrayList<>();

        for (Edge<V> edge : edges) {
            neighbors.add(edge.destination);
        }

        return neighbors;
    }

    // Get vertex count
    @Override
    public int vertexCount() {
        return adjacencyMap.size();
    }

    // Get edge count
    @Override
    public int edgeCount() {
        return edgeCount;
    }

    // Get edge weight
    public double getEdgeWeight(V from, V to) {
        if (!adjacencyMap.containsKey(from)) {
            return -1.0;
        }

        List<Edge<V>> edges = adjacencyMap.get(from);

        for (Edge<V> edge : edges) {
            if (edge.destination.equals(to)) {
                return edge.weight;
            }
        }

        return -1.0;
    }

    // Get all vertices
    public Set<V> getVertices() {
        return adjacencyMap.keySet();
    }

    // Print graph structure
    public void printGraph() {
        System.out.println("=== Graph Structure ===");

        for (Map.Entry<V, List<Edge<V>>> entry : adjacencyMap.entrySet()) {
            V vertex = entry.getKey();
            List<Edge<V>> edges = entry.getValue();

            System.out.print(vertex + " -> ");

            for (Edge<V> edge : edges) {
                System.out.print(
                        "[" + edge.destination + " (" + edge.weight + ")] "
                );
            }

            System.out.println();
        }

        System.out.println("Vertices: " + vertexCount());
        System.out.println("Edges: " + edgeCount());
        System.out.println("======================");
    }
}