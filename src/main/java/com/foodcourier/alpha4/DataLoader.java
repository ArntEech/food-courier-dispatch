package com.foodcourier.alpha4;

import com.foodcourier.dsa.graph.Edge;
import com.foodcourier.dsa.graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DataLoader loads location and road data from CSV files into the graph.
 *
 * UPDATED: builds a Graph<Integer> (dsa.graph.Graph) instead of
 * AdjacencyListGraph<Integer>, to match the canonical graph the whole
 * Alpha 4 team agreed on (Dijkstra/Prim were already built against
 * Graph<V>, so this avoids maintaining two incompatible graphs).
 *
 * Bidirectional roads use graph.addUndirectedEdge() directly, instead
 * of manually adding the edge twice — Graph<V> already handles that
 * internally (addUndirectedEdge calls addEdge in both directions).
 */
public class DataLoader {

    // Location data structure - stores location_id -> location name mapping
    private Map<Integer, String> locationNames;

    // Graph containing all locations as vertices and roads as edges
    private Graph<Integer> graph;

    /**
     * Constructor - initializes data structures
     */
    public DataLoader() {
        locationNames = new HashMap<>();
        graph = new Graph<>();
    }

    /**
     * Main method to load all data and build the graph
     */
    public void loadAllData() {
        System.out.println("Starting data load...");

        // Step 1: Load locations (vertices)
        loadLocations("data/seed/locations.csv");

        // Step 2: Load roads (edges)
        loadRoads("data/seed/roads.csv");

        System.out.println("Data load complete!");
        System.out.println("Locations loaded: " + graph.vertexCount());
        System.out.println("Roads loaded: " + graph.edgeCount());
    }

    /**
     * Loads locations from CSV file and adds them as vertices in the graph.
     *
     * @param filePath Path to the locations CSV file
     */
    public void loadLocations(String filePath) {
        System.out.println("Loading locations from: " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            int locationCount = 0;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.isBlank()) continue;

                // Format: location_id,name,latitude,longitude,location_type
                String[] parts = line.split(",");

                if (parts.length >= 5) {
                    try {
                        int locationId = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();

                        graph.addVertex(locationId);
                        locationNames.put(locationId, name);
                        locationCount++;

                        if (locationCount <= 5) {
                            System.out.println("   Loaded: ID=" + locationId + ", Name=" + name);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing location ID: " + parts[0]);
                    }
                }
            }

            System.out.println("Loaded " + locationCount + " locations");

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            System.err.println("   Make sure you're running from the project root directory");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Loads roads from CSV file and adds them as edges in the graph.
     * Uses addUndirectedEdge() when is_bidirectional = 1, and addEdge()
     * (one direction only) otherwise.
     *
     * @param filePath Path to the roads CSV file
     */
    public void loadRoads(String filePath) {
        System.out.println("Loading roads from: " + filePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            int roadCount = 0;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.isBlank()) continue;

                // Format: road_id,from_location_id,to_location_id,distance_km,travel_time_minutes,is_bidirectional
                String[] parts = line.split(",");

                if (parts.length >= 6) {
                    try {
                        int fromId = Integer.parseInt(parts[1].trim());
                        int toId = Integer.parseInt(parts[2].trim());
                        double distanceKm = Double.parseDouble(parts[3].trim());
                        boolean isBidirectional = parts[5].trim().equals("1");

                        if (isBidirectional) {
                            graph.addUndirectedEdge(fromId, toId, distanceKm);
                        } else {
                            graph.addEdge(fromId, toId, distanceKm);
                        }
                        roadCount++;

                        if (roadCount <= 5) {
                            String fromName = locationNames.getOrDefault(fromId, "Unknown");
                            String toName = locationNames.getOrDefault(toId, "Unknown");
                            System.out.println("   Loaded: " + fromName + " -> " + toName +
                                    " (distance: " + distanceKm + "km, bidirectional: " +
                                    (isBidirectional ? "yes" : "no") + ")");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing road data: " + line);
                    }
                }
            }

            System.out.println("Loaded " + roadCount + " roads");

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            System.err.println("   Make sure you're running from the project root directory");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Gets the loaded graph.
     *
     * @return The graph containing all locations and roads
     */
    public Graph<Integer> getGraph() {
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
     * Helper method to print all locations and roads (for debugging).
     * Reimplemented against Graph<V>'s API (getVertices()/getEdgesFrom())
     * since Graph doesn't have an AdjacencyListGraph-style printGraph().
     */
    public void printDataSummary() {
        System.out.println("\n=== Data Summary ===");
        System.out.println("Total locations: " + graph.vertexCount());
        System.out.println("Total roads: " + graph.edgeCount());

        System.out.println("\nGraph structure:");
        for (Integer vertex : graph.getVertices()) {
            String name = locationNames.getOrDefault(vertex, "Unknown");
            System.out.println("  " + name + " (" + vertex + "):");
            for (Edge<Integer> edge : graph.getEdgesFrom(vertex)) {
                String toName = locationNames.getOrDefault(edge.getTo(), "Unknown");
                System.out.println("    -> " + toName + " (" + edge.getWeight() + " km)");
            }
        }

        System.out.println("\nLocation Names:");
        for (Map.Entry<Integer, String> entry : locationNames.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("===================\n");
    }

    /**
     * Main method for testing the data loader.
     */
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadAllData();
        dataLoader.printDataSummary();

        Graph<Integer> graph = dataLoader.getGraph();
        Map<Integer, String> names = dataLoader.getLocationNames();

        System.out.println("\n=== Testing Graph ===");
        String location1Name = names.getOrDefault(1, "Location 1");
        System.out.println("Neighbors of " + location1Name + " (ID: 1):");
        for (Edge<Integer> edge : graph.getEdgesFrom(1)) {
            String neighborName = names.getOrDefault(edge.getTo(), "Unknown");
            System.out.println("  -> " + neighborName + " (distance: " + edge.getWeight() + " km)");
        }

        System.out.println("\nDataLoader test complete!");
    }
}