package com.foodcourier.algorithms.graph;

import com.foodcourier.dsa.disjointset.DisjointSetInterface;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Kruskal's algorithm, tested in isolation using a minimal
 * in-file DisjointSet test double — deliberately NOT dependent on Caleb's
 * real dsa/disjointset/DisjointSet.java or AdjacencyListGraph.java, so this
 * test is unblocked and can run today.
 *
 * Once Caleb's real DisjointSet is merged, add a second round of these same
 * tests using his implementation instead of TestDisjointSet, to confirm
 * integration (not just Kruskal's own logic) is correct.
 */
public class KruskalTest {

    @Test
    public void minimumSpanningTree_returnsCorrectNumberOfEdges() {
        // Hand-traced graph — expected MST: A-C(1), B-C(2), D-E(2), B-D(5) = weight 10
        List<String> vertices = List.of("A", "B", "C", "D", "E");
        List<Edge<String>> edges = List.of(
                new Edge<>("A", "B", 4),
                new Edge<>("A", "C", 1),
                new Edge<>("B", "C", 2),
                new Edge<>("B", "D", 5),
                new Edge<>("C", "D", 8),
                new Edge<>("C", "E", 10),
                new Edge<>("D", "E", 2)
        );

        Kruskal<String> kruskal = new Kruskal<>();
        List<Edge<String>> mst = kruskal.findMST(vertices, edges, new TestDisjointSet<>());

        assertEquals(vertices.size() - 1, mst.size(), "MST of a connected graph should have V-1 edges");

        double totalWeight = mst.stream().mapToDouble(e -> e.weight).sum();
        assertEquals(10.0, totalWeight, 0.0001, "Total MST weight should match hand-traced value");
    }

    @Test
    public void minimumSpanningTree_singleVertex_returnsNoEdges() {
        List<String> vertices = List.of("A");
        List<Edge<String>> edges = List.of();

        Kruskal<String> kruskal = new Kruskal<>();
        List<Edge<String>> mst = kruskal.findMST(vertices, edges, new TestDisjointSet<>());

        assertTrue(mst.isEmpty(), "A single-vertex graph has no edges to span");
    }

    @Test
    public void minimumSpanningTree_disconnectedGraph_onlySpansReachableEdges() {
        // A-B connected; C is isolated (no edges touch it)
        List<String> vertices = List.of("A", "B", "C");
        List<Edge<String>> edges = List.of(
                new Edge<>("A", "B", 3)
        );

        Kruskal<String> kruskal = new Kruskal<>();
        List<Edge<String>> mst = kruskal.findMST(vertices, edges, new TestDisjointSet<>());

        // Only 1 edge exists at all, so MST can only contain that 1 edge —
        // it cannot connect C since no edge touches it.
        assertEquals(1, mst.size(), "Disconnected graph MST should only include reachable edges");
    }

    @Test
    public void minimumSpanningTree_tieInWeights_stillProducesValidSpanningTree() {
        // B-C and A-D both weight 2 — a tie. Either is acceptable; what
        // matters is the MST is still valid: V-1 edges, correct total weight.
        List<String> vertices = List.of("A", "B", "C", "D");
        List<Edge<String>> edges = List.of(
                new Edge<>("A", "B", 1),
                new Edge<>("B", "C", 2),
                new Edge<>("A", "D", 2),
                new Edge<>("C", "D", 5)
        );

        Kruskal<String> kruskal = new Kruskal<>();
        List<Edge<String>> mst = kruskal.findMST(vertices, edges, new TestDisjointSet<>());

        assertEquals(vertices.size() - 1, mst.size());
        double totalWeight = mst.stream().mapToDouble(e -> e.weight).sum();
        assertEquals(5.0, totalWeight, 0.0001, "A-B(1) + B-C(2) + A-D(2) = 5, the minimum possible");
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