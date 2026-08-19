package com.foodcourier.algorithms.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;
import com.foodcourier.dsa.heap.BinaryHeap;

public final class Prim {

	private Prim() {
	}

	public static <V> Result<V> minimumSpanningTree(Graph<V> graph, V start) {
		if (graph == null || !graph.containsVertex(start)) {
			throw new IllegalArgumentException("Start vertex is not present in the graph.");
		}

		Set<V> visited = new HashSet<>();
		List<Edge<V>> edges = new ArrayList<>();
		BinaryHeap queue = new BinaryHeap();
		addEdges(start, visited, queue, graph);

		while (!queue.isPriorityEmpty()) {
			BinaryHeap.PriorityEntry<?> entry = queue.removeMinPriority();
			@SuppressWarnings("unchecked")
			Edge<V> edge = (Edge<V>) entry.getItem();
			if (visited.contains(edge.getTo())) {
				continue;
			}
			edges.add(edge);
			addEdges(edge.getTo(), visited, queue, graph);
		}
		return new Result<>(edges, graph.vertexCount(), visited.size());
	}

	private static <V> void addEdges(V vertex, Set<V> visited, BinaryHeap queue, Graph<V> graph) {
		visited.add(vertex);
		for (Edge<V> edge : graph.getEdgesFrom(vertex)) {
			if (!visited.contains(edge.getTo())) {
				queue.insertPriority(edge, edge.getWeight());
			}
		}
	}

	public static final class Result<V> {

		private final List<Edge<V>> edges;
		private final int vertexCount;
		private final int reachedVertexCount;

		private Result(List<Edge<V>> edges, int vertexCount, int reachedVertexCount) {
			this.edges = List.copyOf(edges);
			this.vertexCount = vertexCount;
			this.reachedVertexCount = reachedVertexCount;
		}

		public List<Edge<V>> getEdges() {
			return edges;
		}

		public double getTotalWeight() {
			return edges.stream().mapToDouble(Edge::getWeight).sum();
		}

		public boolean connectsAllVertices() {
			return reachedVertexCount == vertexCount;
		}
	}
}
