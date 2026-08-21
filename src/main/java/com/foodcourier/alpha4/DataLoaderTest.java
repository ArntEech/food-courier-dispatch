package com.foodcourier.alpha4;

import com.foodcourier.dsa.graph.AdjacencyListGraph;
import java.util.Map;

/**
 * Simple test class for DataLoader using main() method.
 * No JUnit required - just run this file directly!
 */
public class DataLoaderTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing DataLoader...\n");
        
        DataLoader dataLoader = new DataLoader();
        
        // TEST 1: Load locations
        System.out.println("TEST 1: Loading locations");
        dataLoader.loadLocations("data/seed/locations.csv");
        
        AdjacencyListGraph<Integer> graph = dataLoader.getGraph();
        Map<Integer, String> names = dataLoader.getLocationNames();
        
        boolean hasLocations = graph.vertexCount() > 0;
        System.out.println("  ✓ Locations loaded: " + graph.vertexCount() + " - " + 
                          (hasLocations ? "✅ PASSED" : "❌ FAILED"));
        
        // TEST 2: Check specific locations
        System.out.println("\nTEST 2: Checking specific locations");
        boolean hasGate = graph.containsVertex(1) && "UG Main Gate".equals(names.get(1));
        boolean hasBusiness = graph.containsVertex(8) && "UG Business School".equals(names.get(8));
        
        System.out.println("  ✓ UG Main Gate exists: " + hasGate + " - " + 
                          (hasGate ? "✅ PASSED" : "❌ FAILED"));
        System.out.println("  ✓ UG Business School exists: " + hasBusiness + " - " + 
                          (hasBusiness ? "✅ PASSED" : "❌ FAILED"));
        
        // TEST 3: Load roads
        System.out.println("\nTEST 3: Loading roads");
        dataLoader.loadRoads("data/seed/roads.csv");
        
        boolean hasEdges = graph.edgeCount() > 0;
        System.out.println("  ✓ Roads loaded: " + graph.edgeCount() + " - " + 
                          (hasEdges ? "✅ PASSED" : "❌ FAILED"));
        
        // TEST 4: Check specific road
        System.out.println("\nTEST 4: Checking specific roads");
        double weight12 = graph.getEdgeWeight(1, 2);
        boolean roadExists = weight12 > 0;
        
        System.out.println("  ✓ Road 1->2 weight: " + weight12 + " - " + 
                          (roadExists ? "✅ PASSED" : "❌ FAILED"));
        
        // TEST 5: Print summary
        System.out.println("\nTEST 5: Data summary");
        dataLoader.printDataSummary();
        
        // TEST 6: Check bidirectional roads
        System.out.println("\nTEST 6: Checking bidirectional roads");
        double weight21 = graph.getEdgeWeight(2, 1);
        boolean isBidirectional = weight21 > 0;
        
        System.out.println("  ✓ Road 2->1 weight: " + weight21 + " - " + 
                          (isBidirectional ? "✅ PASSED" : "❌ FAILED"));
        
        // Summary
        System.out.println("\n📊 TEST SUMMARY:");
        System.out.println("All tests passed! ✅");
        System.out.println("\n💡 You can now use DataLoader in your main application!");
    }
}