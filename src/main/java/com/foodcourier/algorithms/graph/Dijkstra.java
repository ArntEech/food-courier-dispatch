package com.foodcourier.algorithms.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.foodcourier.alpha2.BinaryHeap;
import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

public class Dijkstra {

    private static final double EPSILON = 1e-9;

    private Dijkstra() {
        // Utility class.
    }

    public static <V> Map<V, Double> shortestPaths(Graph<V> graph, V source) {
        SearchResult<V> result = run(graph, source);
        return result.distances;
    }

    public static <V> double shortestDistance(Graph<V> graph, V source, V target) {
        if (graph == null || source == null || target == null) {
            return Double.POSITIVE_INFINITY;
        }
        if (source.equals(target)) {
            return 0.0;
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            return Double.POSITIVE_INFINITY;
        }

        SearchResult<V> result = run(graph, source);
        return result.distances.getOrDefault(target, Double.POSITIVE_INFINITY);
    }

    public static <V> List<V> shortestPath(Graph<V> graph, V source, V target) {
        if (graph == null || source == null || target == null) {
            return Collections.emptyList();
        }
        if (source.equals(target)) {
            return new ArrayList<>(Collections.singletonList(source));
        }
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            return Collections.emptyList();
        }

        SearchResult<V> result = run(graph, source);
        if (Double.isInfinite(result.distances.getOrDefault(target, Double.POSITIVE_INFINITY))) {
            return Collections.emptyList();
        }

        List<V> path = new ArrayList<>();
        V current = target;
        while (current != null) {
            path.add(current);
            if (current.equals(source)) {
                break;
            }
            current = result.previous.get(current);
        }

        if (path.isEmpty() || !path.get(path.size() - 1).equals(source)) {
            return Collections.emptyList();
        }

        Collections.reverse(path);
        return path;
    }

    private static <V> SearchResult<V> run(Graph<V> graph, V source) {
        if (graph == null || source == null || !graph.containsVertex(source)) {
            return new SearchResult<>(new LinkedHashMap<>(), new LinkedHashMap<>());
        }

        Map<V, Double> distances = new LinkedHashMap<>();
        Map<V, V> previous = new LinkedHashMap<>();
        BinaryHeap<HeapEntry<V>> heap = new BinaryHeap<>(Comparator.comparingDouble(entry -> entry.distance));

        for (V vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        heap.insert(new HeapEntry<>(source, 0.0));

        while (!heap.isEmpty()) {
            HeapEntry<V> current = heap.removeMin();
            double currentDistance = distances.getOrDefault(current.vertex, Double.POSITIVE_INFINITY);

            // Ignore outdated heap entries left behind by a shorter path update.
            if (current.distance > currentDistance + EPSILON) {
                continue;
            }

            for (Edge<V> edge : graph.getEdgesFrom(current.vertex)) {
                V neighbor = edge.getTo();
                double candidateDistance = currentDistance + edge.getWeight();

                if (candidateDistance < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbor, candidateDistance);
                    previous.put(neighbor, current.vertex);
                    heap.insert(new HeapEntry<>(neighbor, candidateDistance));
                }
            }
        }

        return new SearchResult<>(distances, previous);
    }

    private static final class HeapEntry<V> {
        private final V vertex;
        private final double distance;

        private HeapEntry(V vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    private static final class SearchResult<V> {
        private final Map<V, Double> distances;
        private final Map<V, V> previous;

        private SearchResult(Map<V, Double> distances, Map<V, V> previous) {
            this.distances = distances;
            this.previous = previous;
        }
    }
}
