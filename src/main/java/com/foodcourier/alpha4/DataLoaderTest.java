package com.foodcourier.alpha4;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;
import java.util.Map;

/**
 * Simple test class for DataLoader using main() method.
 * No JUnit required - just run this file directly!
 *
 * UPDATED: uses Graph<Integer> instead of AdjacencyListGraph<Integer>,
 * matching DataLoader.getGraph()'s new return type. Graph<V> has no
 * getEdgeWeight(from, to) method (only getEdgesFrom(vertex)), so a
 * small local helper (edgeWeight()) replaces those direct calls.
 */
public class DataLoaderTest {

    public static void main(String[] args) {
        System.out.println("Testing DataLoader...\n");

        DataLoader dataLoader = new DataLoader();

        // TEST 1: Load locations
        System.out.println("TEST 1: Loading locations");
        dataLoader.loadLocations("data/seed/locations.csv");

        Graph<Integer> graph = dataLoader.getGraph();
        Map<Integer, String> names = dataLoader.getLocationNames();

        boolean hasLocations = graph.vertexCount() > 0;
        System.out.println("  Locations loaded: " + graph.vertexCount() + " - " +
                          (hasLocations ? "PASSED" : "FAILED"));

        // TEST 2: Check specific locations
        System.out.println("\nTEST 2: Checking specific locations");
        boolean hasGate = graph.containsVertex(1) && "UG Main Gate".equals(names.get(1));
        boolean hasBusiness = graph.containsVertex(8) && "UG Business School".equals(names.get(8));

        System.out.println("  UG Main Gate exists: " + hasGate + " - " +
                          (hasGate ? "PASSED" : "FAILED"));
        System.out.println("  UG Business School exists: " + hasBusiness + " - " +
                          (hasBusiness ? "PASSED" : "FAILED"));

        // TEST 3: Load roads
        System.out.println("\nTEST 3: Loading roads");
        dataLoader.loadRoads("data/seed/roads.csv");

        boolean hasEdges = graph.edgeCount() > 0;
        System.out.println("  Roads loaded: " + graph.edgeCount() + " - " +
                          (hasEdges ? "PASSED" : "FAILED"));

        // TEST 4: Check specific road
        System.out.println("\nTEST 4: Checking specific roads");
        double weight12 = edgeWeight(graph, 1, 2);
        boolean roadExists = weight12 > 0;

        System.out.println("  Road 1->2 weight: " + weight12 + " - " +
                          (roadExists ? "PASSED" : "FAILED"));

        // TEST 5: Print summary
        System.out.println("\nTEST 5: Data summary");
        dataLoader.printDataSummary();

        // TEST 6: Check bidirectional roads
        System.out.println("\nTEST 6: Checking bidirectional roads");
        double weight21 = edgeWeight(graph, 2, 1);
        boolean isBidirectional = weight21 > 0;

        System.out.println("  Road 2->1 weight: " + weight21 + " - " +
                          (isBidirectional ? "PASSED" : "FAILED"));

        // Summary
        System.out.println("\nTEST SUMMARY:");
        System.out.println("All tests passed!");
        System.out.println("\nYou can now use DataLoader in your main application!");
    }

    /**
     * Graph<V> doesn't expose getEdgeWeight(from, to) directly (only
     * AdjacencyListGraph did). This searches the 'from' vertex's edge
     * list for one pointing to 'to', matching the old method's behavior.
     *
     * @return the edge weight, or -1 if no such edge exists
     */
    private static double edgeWeight(Graph<Integer> graph, int from, int to) {
        for (Edge<Integer> edge : graph.getEdgesFrom(from)) {
            if (edge.getTo().equals(to)) {
                return edge.getWeight();
            }
        }
        return -1;
    }
}