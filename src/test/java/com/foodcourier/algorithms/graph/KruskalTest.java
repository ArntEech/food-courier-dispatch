package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Kruskal's algorithm, now testing against the canonical
 * dsa.graph.Graph<V> (matching Dijkstra/Prim's tests) instead of a
 * hand-built edge list.
 *
 * Uses a minimal in-file DisjointSet test double so these tests don't
 * depend on Caleb's real DisjointSet being available — though in
 * practice it now is, and passes against it too (see
 * KruskalIntegrationTest if/when that's added for a real-DisjointSet
 * cross-check).
 */
public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectEdgesAndWeight() {
        // Same hand-traced graph used throughout Alpha 4 —
        // expected MST: A-C(1), B-C(2), D-E(2), B-D(5) = weight 10
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 4);
        graph.addUndirectedEdge("A", "C", 1);
        graph.addUndirectedEdge("B", "C", 2);
        graph.addUndirectedEdge("B", "D", 5);
        graph.addUndirectedEdge("C", "D", 8);
        graph.addUndirectedEdge("C", "E", 10);
        graph.addUndirectedEdge("D", "E", 2);

        Kruskal.Result<String> result = Kruskal.minimumSpanningTree(graph, new TestDisjointSet<>());

        assertEquals(4, result.getEdges().size(), "MST of 5 vertices should have V-1 = 4 edges");
        assertEquals(10.0, result.getTotalWeight(), 0.0001, "Total MST weight should match hand-traced value");
        assertTrue(result.connectsAllVertices());
    }

    @Test
    public void minimumSpanningTree_matchesPrimOnSameGraph() {
        // Cross-check: Kruskal and Prim should always agree on total MST
        // weight for a connected graph, even if the exact edges picked differ.
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 4);
        graph.addUndirectedEdge("A", "C", 1);
        graph.addUndirectedEdge("B", "C", 2);
        graph.addUndirectedEdge("B", "D", 5);
        graph.addUndirectedEdge("C", "D", 8);
        graph.addUndirectedEdge("C", "E", 10);
        graph.addUndirectedEdge("D", "E", 2);

        Kruskal.Result<String> kruskalResult = Kruskal.minimumSpanningTree(graph, new TestDisjointSet<>());
        Prim.Result<String> primResult = Prim.minimumSpanningTree(graph, "A");

        assertEquals(primResult.getTotalWeight(), kruskalResult.getTotalWeight(), 0.0001,
                "Kruskal and Prim must agree on total MST weight");
    }

    @Test
    public void minimumSpanningTree_singleVertex_returnsNoEdges() {
        Graph<String> graph = new Graph<>();
        graph.addVertex("A");

        Kruskal.Result<String> result = Kruskal.minimumSpanningTree(graph, new TestDisjointSet<>());

        assertTrue(result.getEdges().isEmpty(), "A single-vertex graph has no edges to span");
        assertTrue(result.connectsAllVertices(), "Trivially true: 0 edges = 0 = vertexCount(1) - 1");
    }

    @Test
    public void minimumSpanningTree_disconnectedGraph_doesNotConnectAllVertices() {
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 3);
        graph.addVertex("C"); // isolated — no edges touch it

        Kruskal.Result<String> result = Kruskal.minimumSpanningTree(graph, new TestDisjointSet<>());

        assertEquals(1, result.getEdges().size(), "Only 1 edge exists at all, C cannot be reached");
        assertFalse(result.connectsAllVertices(), "3 vertices but only 1 edge means C is unreachable");
    }

    @Test
    public void minimumSpanningTree_tieInWeights_stillProducesValidSpanningTree() {
        // B-C and A-D both weight 2 — a tie. Either is acceptable; what
        // matters is the MST is still valid: correct edge count, correct
        // (minimum possible) total weight.
        Graph<String> graph = new Graph<>();
        graph.addUndirectedEdge("A", "B", 1);
        graph.addUndirectedEdge("B", "C", 2);
        graph.addUndirectedEdge("A", "D", 2);
        graph.addUndirectedEdge("C", "D", 5);

        Kruskal.Result<String> result = Kruskal.minimumSpanningTree(graph, new TestDisjointSet<>());

        assertEquals(3, result.getEdges().size());
        assertEquals(5.0, result.getTotalWeight(), 0.0001, "A-B(1) + B-C(2) + A-D(2) = 5, the minimum possible");
    }

    /**
     * Minimal DisjointSet test double, scoped to this test file only.
     * Not the assessed component — Caleb's real DisjointSet is what ships.
     */
    private static class TestDisjointSet<E> implements DisjointSetInterface<E> {
        private final Map<E, E> parent = new HashMap<>();

        @Override
        public void makeSet(E item) {
            parent.put(item, item);
        }

        @Override
        public E find(E item) {
            E p = parent.get(item);
            if (p == null) {
                makeSet(item);
                return item;
            }
            if (p.equals(item)) return item;
            E root = find(p);
            parent.put(item, root);
            return root;
        }

        @Override
        public void union(E first, E second) {
            E rootA = find(first);
            E rootB = find(second);
            if (!rootA.equals(rootB)) {
                parent.put(rootA, rootB);
            }
        }

        @Override
        public boolean connected(E first, E second) {
            return find(first).equals(find(second));
        }
    }
}
