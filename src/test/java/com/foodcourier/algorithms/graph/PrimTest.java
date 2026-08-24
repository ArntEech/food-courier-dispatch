package com.foodcourier.algorithms.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.dsa.graph.Graph;

class PrimTest {

    @Test
    void buildsMinimumSpanningTreeForConnectedWeightedGraph() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 1);
        graph.addUndirectedEdge("A", "C", 4);
        graph.addUndirectedEdge("B", "C", 2);
        graph.addUndirectedEdge("B", "D", 5);
        graph.addUndirectedEdge("C", "D", 3);

        Prim.Result<String> result = Prim.minimumSpanningTree(graph, "A");

        assertNotNull(result);
        assertEquals(3, result.getEdges().size());
        assertEquals(6.0, result.getTotalWeight(), 0.0001);
        assertTrue(result.connectsAllVertices());
    }

    @Test
    void handlesSeveralDifferentWeights() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 4);
        graph.addUndirectedEdge("A", "C", 2);
        graph.addUndirectedEdge("B", "C", 1);
        graph.addUndirectedEdge("B", "D", 7);
        graph.addUndirectedEdge("C", "D", 3);
        graph.addUndirectedEdge("C", "E", 8);
        graph.addUndirectedEdge("D", "E", 2);

        Prim.Result<String> result = Prim.minimumSpanningTree(graph, "A");

        assertNotNull(result);
        assertEquals(4, result.getEdges().size());
        assertEquals(8.0, result.getTotalWeight(), 0.0001);
        assertTrue(result.connectsAllVertices());
    }

    @Test
    void returnsForestForDisconnectedGraph() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 5);
        graph.addVertex("C");

        Prim.Result<String> result = Prim.minimumSpanningTree(graph, "A");

        assertNotNull(result);
        assertEquals(1, result.getEdges().size());
        assertEquals(5.0, result.getTotalWeight(), 0.0001);
        assertFalse(result.connectsAllVertices());
    }
}
