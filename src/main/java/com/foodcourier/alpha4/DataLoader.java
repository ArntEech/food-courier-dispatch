package com.foodcourier.alpha4;

import com.foodcourier.dsa.graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DataLoader loads location and road data from CSV files into the graph.
 *
 * UPDATED: now builds a Graph<Integer> (dsa.graph.Graph) instead of
 * AdjacencyListGraph<Integer>, to match the canonical graph the whole
 * Alpha 4 team agreed on (Dijkstra/Prim were already built against
 * Graph<V>, so this avoids maintaining two incompatible graphs).
 *
 * Bidirectional roads now use graph.addUndirectedEdge() directly,
 * instead of manually adding the edge twice — Graph<V> already handles
 * that internally (addUndirectedEdge calls addEdge in both directions).
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
    private void loadLocations(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                int locationId = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();

                graph.addVertex(locationId);
                locationNames.put(locationId, name);
            }
        } catch (IOException e) {
            System.out.println("Error loading locations: " + e.getMessage());
        }
    }

    /**
     * Loads roads from CSV file and adds them as edges in the graph.
     * Uses addUndirectedEdge() when is_bidirectional = 1, and addEdge()
     * (one direction only) otherwise.
     *
     * @param filePath Path to the roads CSV file
     */
    private void loadRoads(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");

                int fromId = Integer.parseInt(parts[1].trim());
                int toId = Integer.parseInt(parts[2].trim());
                double distanceKm = Double.parseDouble(parts[3].trim());
                boolean isBidirectional = parts[5].trim().equals("1");

                if (isBidirectional) {
                    graph.addUndirectedEdge(fromId, toId, distanceKm);
                } else {
                    graph.addEdge(fromId, toId, distanceKm);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading roads: " + e.getMessage());
        }
    }

    public Graph<Integer> getGraph() {
        return graph;
    }

    public Map<Integer, String> getLocationNames() {
        return locationNames;
    }
}
