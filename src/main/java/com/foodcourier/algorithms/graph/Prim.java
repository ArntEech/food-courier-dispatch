package com.foodcourier.algorithms.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.foodcourier.alpha2.BinaryHeap;
import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

public class Prim {

    private Prim() {
        // Utility class.
    }

    public static <V> Result<V> minimumSpanningTree(Graph<V> graph, V startVertex) {
        Result<V> result = new Result<>();

        if (graph == null || startVertex == null || !graph.containsVertex(startVertex)) {
            return result;
        }

        Set<V> visited = new HashSet<>();
        Map<V, Double> minWeight = new LinkedHashMap<>();
        Map<V, Edge<V>> bestEdge = new LinkedHashMap<>();
        BinaryHeap<HeapEntry<V>> heap = new BinaryHeap<>(Comparator.comparingDouble(entry -> entry.weight));

        for (V vertex : graph.getVertices()) {
            minWeight.put(vertex, Double.POSITIVE_INFINITY);
        }
        minWeight.put(startVertex, 0.0);
        visited.add(startVertex);

        // Seed the heap with every edge from the starting vertex so the smallest candidate is chosen first.
        for (Edge<V> edge : graph.getEdgesFrom(startVertex)) {
            V neighbor = edge.getTo();
            if (!visited.contains(neighbor)) {
                minWeight.put(neighbor, edge.getWeight());
                bestEdge.put(neighbor, edge);
                heap.insert(new HeapEntry<>(neighbor, edge.getWeight(), edge));
            }
        }

        while (!heap.isEmpty() && visited.size() < graph.vertexCount()) {
            HeapEntry<V> entry = heap.removeMin();
            V candidate = entry.vertex;

            if (visited.contains(candidate)) {
                continue;
            }

            visited.add(candidate);
            result.addEdge(bestEdge.get(candidate));

            for (Edge<V> edge : graph.getEdgesFrom(candidate)) {
                V neighbor = edge.getTo();
                if (visited.contains(neighbor)) {
                    continue;
                }

                double candidateWeight = edge.getWeight();
                if (candidateWeight < minWeight.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    minWeight.put(neighbor, candidateWeight);
                    bestEdge.put(neighbor, edge);
                    heap.insert(new HeapEntry<>(neighbor, candidateWeight, edge));
                }
            }
        }

        result.finish(graph);
        return result;
    }

    public static final class Result<V> {
        private final List<Edge<V>> edges = new ArrayList<>();
        private double totalWeight;
        private int vertexCount;

        private void addEdge(Edge<V> edge) {
            if (edge != null) {
                edges.add(edge);
                totalWeight += edge.getWeight();
            }
        }

        private void finish(Graph<V> graph) {
            this.vertexCount = graph == null ? 0 : graph.vertexCount();
        }

        public List<Edge<V>> getEdges() {
            return new ArrayList<>(edges);
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public boolean connectsAllVertices() {
            return edges.size() == Math.max(0, vertexCount - 1);
        }
    }

    private static final class HeapEntry<V> {
        private final V vertex;
        private final double weight;
        private final Edge<V> edge;

        private HeapEntry(V vertex, double weight, Edge<V> edge) {
            this.vertex = vertex;
            this.weight = weight;
            this.edge = edge;
        }
    }
}
