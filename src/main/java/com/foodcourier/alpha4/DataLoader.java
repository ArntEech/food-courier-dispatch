package com.foodcourier.alpha4;

import com.foodcourier.dsa.graph.AdjacencyListGraph;

import java.io.*;
import java.util.*;

/**
 * DataLoader class loads location and road data from CSV files into the graph.
 * This is beginner-friendly with clear comments for each step!
 */
public class DataLoader {
    
    // Location data structure - stores location_id -> location name mapping
    private Map<Integer, String> locationNames;
    
    // Graph containing all locations as vertices and roads as edges
    private AdjacencyListGraph<Integer> graph;
    
    /**
     * Constructor - initializes data structures
     */
    public DataLoader() {
        locationNames = new HashMap<>();
        graph = new AdjacencyListGraph<>();
    }
    
    /**
     * Main method to load all data and build the graph
     */
    public void loadAllData() {
        System.out.println("🚀 Starting data load...");
        
        // Step 1: Load locations (vertices)
        loadLocations("data/seed/locations.csv");
        
        // Step 2: Load roads (edges)
        loadRoads("data/seed/roads.csv");
        
        System.out.println("✅ Data load complete!");
        System.out.println("Locations loaded: " + graph.vertexCount());
        System.out.println("Roads loaded: " + graph.edgeCount());
    }
    
    /**
     * Loads locations from CSV file and adds them as vertices in the graph.
     * 
     * @param filePath Path to the locations CSV file
     */
    public void loadLocations(String filePath) {
        System.out.println("📂 Loading locations from: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            int locationCount = 0;
            
            // Read file line by line
            while ((line = reader.readLine()) != null) {
                // Skip the header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Split the line by comma
                // Format: location_id,name,latitude,longitude,location_type
                String[] parts = line.split(",");
                
                if (parts.length >= 5) {
                    try {
                        // Parse location ID
                        int locationId = Integer.parseInt(parts[0].trim());
                        
                        // Get location name
                        String name = parts[1].trim();
                        
                        // Store in map for later reference
                        locationNames.put(locationId, name);
                        
                        // Add as vertex in the graph
                        graph.addVertex(locationId);
                        
                        locationCount++;
                        
                        // Debug: Print first few locations
                        if (locationCount <= 5) {
                            System.out.println("   Loaded: ID=" + locationId + ", Name=" + name);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error parsing location ID: " + parts[0]);
                    }
                }
            }
            
            System.out.println("✅ Loaded " + locationCount + " locations");
            
        } catch (FileNotFoundException e) {
            System.err.println("❌ File not found: " + filePath);
            System.err.println("   Make sure you're running from the project root directory");
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Loads roads from CSV file and adds them as edges in the graph.
     * 
     * @param filePath Path to the roads CSV file
     */
    public void loadRoads(String filePath) {
        System.out.println("📂 Loading roads from: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            int roadCount = 0;
            
            // Read file line by line
            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Split the line by comma
                // Format: road_id,from_location_id,to_location_id,distance_km,travel_time_minutes,is_bidirectional
                String[] parts = line.split(",");
                
                if (parts.length >= 6) {
                    try {
                        // Parse road data
                        int fromId = Integer.parseInt(parts[1].trim());
                        int toId = Integer.parseInt(parts[2].trim());
                        double distanceKm = Double.parseDouble(parts[3].trim());
                        int travelTimeMinutes = Integer.parseInt(parts[4].trim());
                        int isBidirectional = Integer.parseInt(parts[5].trim());
                        
                        // Add edge with distance as weight
                        graph.addEdge(fromId, toId, distanceKm);
                        roadCount++;
                        
                        // If bidirectional, add edge in reverse direction too
                        if (isBidirectional == 1) {
                            graph.addEdge(toId, fromId, distanceKm);
                            roadCount++;
                        }
                        
                        // Debug: Print first few roads
                        if (roadCount <= 5) {
                            String fromName = locationNames.getOrDefault(fromId, "Unknown");
                            String toName = locationNames.getOrDefault(toId, "Unknown");
                            System.out.println("   Loaded: " + fromName + " -> " + toName + 
                                             " (distance: " + distanceKm + "km, bidirectional: " + 
                                             (isBidirectional == 1 ? "yes" : "no") + ")");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error parsing road data: " + line);
                    }
                }
            }
            
            System.out.println("✅ Loaded " + roadCount + " roads (edges)");
            
        } catch (FileNotFoundException e) {
            System.err.println("❌ File not found: " + filePath);
            System.err.println("   Make sure you're running from the project root directory");
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Gets the loaded graph.
     * 
     * @return The graph containing all locations and roads
     */
    public AdjacencyListGraph<Integer> getGraph() {
        return graph;
    }
    
    /**
     * Gets the location name mapping.
     * 
     * @return Map of location ID to location name
     */
    public Map<Integer, String> getLocationNames() {
        return locationNames;
    }
    
    /**
     * Helper method to print all locations and roads (for debugging)
     */
    public void printDataSummary() {
        System.out.println("\n=== Data Summary ===");
        System.out.println("Total locations: " + graph.vertexCount());
        System.out.println("Total roads: " + graph.edgeCount());
        
        // Print the graph structure
        graph.printGraph();
        
        // Print all location names
        System.out.println("\nLocation Names:");
        for (Map.Entry<Integer, String> entry : locationNames.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("===================\n");
    }
    
    /**
     * Main method for testing the data loader
     */
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        
        // Load all data
        dataLoader.loadAllData();
        
        // Print summary
        dataLoader.printDataSummary();
        
        // Test some graph operations
        AdjacencyListGraph<Integer> graph = dataLoader.getGraph();
        Map<Integer, String> names = dataLoader.getLocationNames();
        
        System.out.println("\n=== Testing Graph ===");
        
        // Test neighbors for location 1 (UG Main Gate)
        String location1Name = names.getOrDefault(1, "Location 1");
        System.out.println("Neighbors of " + location1Name + " (ID: 1):");
        for (Integer neighbor : graph.getNeighbors(1)) {
            String neighborName = names.getOrDefault(neighbor, "Unknown");
            double weight = graph.getEdgeWeight(1, neighbor);
            System.out.println("  -> " + neighborName + " (distance: " + weight + " km)");
        }
        
        System.out.println("\n✅ DataLoader test complete!");
    }
}