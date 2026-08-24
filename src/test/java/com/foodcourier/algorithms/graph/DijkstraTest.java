package com.foodcourier.algorithms.graph;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.dsa.graph.Graph;

class DijkstraTest {

    @Test
    void shortestDistanceAndPathAreCalculatedForWeightedGraph() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 4);
        graph.addUndirectedEdge("A", "C", 2);
        graph.addUndirectedEdge("B", "C", 1);
        graph.addUndirectedEdge("B", "D", 5);
        graph.addUndirectedEdge("C", "D", 1);

        Map<String, Double> distances = Dijkstra.shortestPaths(graph, "A");

        assertEquals(0.0, distances.get("A"), 0.0001);
        assertEquals(3.0, distances.get("D"), 0.0001);
        assertEquals(List.of("A", "C", "D"), Dijkstra.shortestPath(graph, "A", "D"));
    }

    @Test
    void choosesLowestCostRouteWhenMultiplePathsExist() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 2);
        graph.addUndirectedEdge("A", "C", 9);
        graph.addUndirectedEdge("B", "C", 1);
        graph.addUndirectedEdge("B", "D", 3);
        graph.addUndirectedEdge("C", "D", 1);

        assertEquals(4.0, Dijkstra.shortestDistance(graph, "A", "D"), 0.0001);
        assertEquals(List.of("A", "B", "C", "D"), Dijkstra.shortestPath(graph, "A", "D"));
    }

    @Test
    void unreachableVertexRetainsInfinityDistance() {
        Graph<String> graph = new Graph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 5);

        Map<String, Double> distances = Dijkstra.shortestPaths(graph, "A");

        assertEquals(Double.POSITIVE_INFINITY, distances.get("C"), 0.0001);
        assertTrue(Double.isInfinite(Dijkstra.shortestDistance(graph, "A", "C")));
    }

    @Test
    void sourceToItselfHasZeroDistance() {
        Graph<String> graph = new Graph<>();
        graph.addVertex("S");
        graph.addVertex("X");

        graph.addEdge("S", "X", 4);

        assertEquals(0.0, Dijkstra.shortestDistance(graph, "S", "S"), 0.0001);
        assertEquals(List.of("S"), Dijkstra.shortestPath(graph, "S", "S"));
    }

    @Test
    void handlesMultipleVerticesWithDifferentEdgeWeights() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 10);
        graph.addUndirectedEdge("A", "C", 3);
        graph.addUndirectedEdge("C", "B", 1);
        graph.addUndirectedEdge("C", "D", 2);
        graph.addUndirectedEdge("B", "D", 4);
        graph.addUndirectedEdge("D", "E", 8);
        graph.addUndirectedEdge("C", "E", 7);

        Map<String, Double> distances = Dijkstra.shortestPaths(graph, "A");

        assertEquals(5.0, distances.get("D"), 0.0001);
        assertEquals(10.0, distances.get("E"), 0.0001);
        assertEquals(List.of("A", "C", "E"), Dijkstra.shortestPath(graph, "A", "E"));
    }
}
