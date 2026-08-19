package com.foodcourier.algorithms.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.dsa.heap.BinaryHeap;

public final class Dijkstra {

	private Dijkstra() {
	}

	public static <V> Map<V, Double> shortestPaths(Graph<V> graph, V source) {
		requireVertex(graph, source);
		Map<V, Double> distances = new LinkedHashMap<>();
		for (V vertex : graph.getVertices()) {
			distances.put(vertex, Double.POSITIVE_INFINITY);
		}
		distances.put(source, 0.0);

		BinaryHeap queue = new BinaryHeap();
		queue.insertPriority(source, 0.0);
		while (!queue.isPriorityEmpty()) {
			BinaryHeap.PriorityEntry<?> entry = queue.removeMinPriority();
			@SuppressWarnings("unchecked")
			V vertex = (V) entry.getItem();
			double distance = entry.getPriority();
			if (distance > distances.get(vertex)) {
				continue;
			}
			for (Edge<V> edge : graph.getEdgesFrom(vertex)) {
				if (edge.getWeight() < 0) {
					throw new IllegalArgumentException("Dijkstra cannot use negative edge weights.");
				}
				double candidate = distance + edge.getWeight();
				if (candidate < distances.get(edge.getTo())) {
					distances.put(edge.getTo(), candidate);
					queue.insertPriority(edge.getTo(), candidate);
				}
			}
		}
		return distances;
	}

	public static <V> double shortestDistance(Graph<V> graph, V source, V target) {
		requireVertex(graph, target);
		return shortestPaths(graph, source).get(target);
	}

	public static <V> List<V> shortestPath(Graph<V> graph, V source, V target) {
		requireVertex(graph, source);
		requireVertex(graph, target);
		Map<V, Double> distances = shortestPaths(graph, source);
		if (Double.isInfinite(distances.get(target))) {
			return Collections.emptyList();
		}

		List<V> path = new ArrayList<>();
		V current = target;
		path.add(current);
		while (!current.equals(source)) {
			V predecessor = null;
			for (V candidate : graph.getVertices()) {
				for (Edge<V> edge : graph.getEdgesFrom(candidate)) {
					if (edge.getTo().equals(current)
							&& Math.abs(distances.get(candidate) + edge.getWeight()
							- distances.get(current)) < 1.0e-9) {
						predecessor = candidate;
						break;
					}
				}
				if (predecessor != null) {
					break;
				}
			}
			if (predecessor == null) {
				return Collections.emptyList();
			}
			current = predecessor;
			path.add(current);
		}
		Collections.reverse(path);
		return path;
	}

	private static <V> void requireVertex(Graph<V> graph, V vertex) {
		if (graph == null || !graph.containsVertex(vertex)) {
			throw new IllegalArgumentException("Vertex is not present in the graph.");
		}
	}
}
