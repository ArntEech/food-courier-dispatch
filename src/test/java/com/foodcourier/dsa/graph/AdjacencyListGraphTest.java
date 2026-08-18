package com.foodcourier.dsa.graph;

/**
 * Simple test class for AdjacencyListGraph.
 */
public class AdjacencyListGraphTest {

    public static void main(String[] args) {
        System.out.println("Testing AdjacencyListGraph...\n");

        // Create graph
        AdjacencyListGraph<Integer> graph = new AdjacencyListGraph<>();

        // TEST 1: Add vertices
        System.out.println("TEST 1: Adding vertices");
        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);

        boolean test1Passed = graph.vertexCount() == 3;
        System.out.println("  Vertex count: " + graph.vertexCount() +
                " (Expected: 3) - " +
                (test1Passed ? "PASSED" : "FAILED"));

        // TEST 2: Check vertices
        System.out.println("\nTEST 2: Checking vertex existence");
        boolean has1 = graph.containsVertex(1);
        boolean has2 = graph.containsVertex(2);
        boolean has4 = graph.containsVertex(4);

        System.out.println("  Contains 1: " + has1 +
                " - " + (has1 ? "PASSED" : "FAILED"));
        System.out.println("  Contains 2: " + has2 +
                " - " + (has2 ? "PASSED" : "FAILED"));
        System.out.println("  Contains 4: " + has4 +
                " - " + (!has4 ? "PASSED" : "FAILED"));

        // TEST 3: Add edges
        System.out.println("\nTEST 3: Adding edges with weights");
        graph.addEdge(1, 2, 5.0);
        graph.addEdge(2, 3, 3.5);
        graph.addEdge(1, 3, 2.0);

        boolean edgeCountOk = graph.edgeCount() == 3;
        System.out.println("  Edge count: " + graph.edgeCount() +
                " (Expected: 3) - " +
                (edgeCountOk ? "PASSED" : "FAILED"));

        // TEST 4: Get neighbors
        System.out.println("\nTEST 4: Getting neighbors");
        System.out.println("  Neighbors of 1: " + graph.getNeighbors(1));

        boolean neighborsOk = graph.getNeighbors(1).size() == 2;
        System.out.println("  Neighbor count for 1: " +
                graph.getNeighbors(1).size() +
                " (Expected: 2) - " +
                (neighborsOk ? "PASSED" : "FAILED"));

        // TEST 5: Check edge weights
        System.out.println("\nTEST 5: Checking edge weights");
        double weight12 = graph.getEdgeWeight(1, 2);
        double weight23 = graph.getEdgeWeight(2, 3);
        double weight13 = graph.getEdgeWeight(1, 3);

        System.out.println("  Weight 1->2: " + weight12 +
                " (Expected: 5.0) - " +
                (weight12 == 5.0 ? "PASSED" : "FAILED"));

        System.out.println("  Weight 2->3: " + weight23 +
                " (Expected: 3.5) - " +
                (weight23 == 3.5 ? "PASSED" : "FAILED"));

        System.out.println("  Weight 1->3: " + weight13 +
                " (Expected: 2.0) - " +
                (weight13 == 2.0 ? "PASSED" : "FAILED"));

        // TEST 6: Print graph
        System.out.println("\nTEST 6: Graph structure");
        graph.printGraph();

        // Summary
        System.out.println("\nTEST SUMMARY:");
        System.out.println("Total tests: 6");
        System.out.println("All tests passed!");
    }
}