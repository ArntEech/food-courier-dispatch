package com.foodcourier.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Seeds the SQLite database from the project's seed CSV files.
 *
 * Usage:
 *   mvn compile exec:java -Dexec.mainClass="com.foodcourier.db.DatabaseSeeder" -q
 *
 * Creates/overwrites food_courier.db in the project root.
 */
public class DatabaseSeeder {

    private static final String DB_PATH = "food_courier.db";
    private static final String SCHEMA_PATH = "database/schema.sql";
    private static final String SEED_DIR = "data/seed/";

    public static void main(String[] args) {
        System.out.println("=== Food Courier Database Seeder ===\n");

        // Delete existing DB for fresh seed
        java.io.File dbFile = new java.io.File(DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
            System.out.println("Removed existing database: " + DB_PATH);
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            conn.setAutoCommit(false);

            // 1. Run schema
            System.out.println("[1/7] Creating schema...");
            runSchema(conn);
            System.out.println("  Schema created.");

            // 2. Seed locations
            System.out.println("[2/7] Seeding locations...");
            seedLocations(conn);
            System.out.println("  Locations seeded.");

            // 3. Seed roads
            System.out.println("[3/7] Seeding roads...");
            seedRoads(conn);
            System.out.println("  Roads seeded.");

            // 4. Seed customers
            System.out.println("[4/7] Seeding customers...");
            seedCustomers(conn);
            System.out.println("  Customers seeded.");

            // 5. Seed restaurants
            System.out.println("[5/7] Seeding restaurants...");
            seedRestaurants(conn);
            System.out.println("  Restaurants seeded.");

            // 6. Seed couriers
            System.out.println("[6/7] Seeding couriers...");
            seedCouriers(conn);
            System.out.println("  Couriers seeded.");

            // 7. Seed orders
            System.out.println("[7/7] Seeding orders...");
            seedOrders(conn);
            System.out.println("  Orders seeded.");

            conn.commit();
            System.out.println("\n=== Database seeded successfully: " + DB_PATH + " ===");

            // Print summary
            printSummary(conn);

        } catch (SQLException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runSchema(Connection conn) throws IOException, SQLException {
        String schema = java.nio.file.Files.readString(Paths.get(SCHEMA_PATH));
        // Strip comment lines, then split by semicolons
        StringBuilder cleaned = new StringBuilder();
        for (String line : schema.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                cleaned.append(line).append("\n");
            }
        }
        try (Statement stmt = conn.createStatement()) {
            for (String sql : cleaned.toString().split(";")) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }

    private static void seedLocations(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO locations (id, name, latitude, longitude, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "locations.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 5) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setDouble(3, Double.parseDouble(p[2].trim()));
                ps.setDouble(4, Double.parseDouble(p[3].trim()));
                ps.setString(5, p[4].trim().toUpperCase());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedRoads(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO roads (id, from_location_id, to_location_id, distance_km, travel_time_min, is_bidirectional) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "roads.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 6) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setInt(2, Integer.parseInt(p[1].trim()));
                ps.setInt(3, Integer.parseInt(p[2].trim()));
                ps.setDouble(4, Double.parseDouble(p[3].trim()));
                ps.setInt(5, Integer.parseInt(p[4].trim()));
                ps.setInt(6, Integer.parseInt(p[5].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedCustomers(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO customers (id, name, phone, location_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "customers.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 4) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setString(3, p[2].trim());
                ps.setInt(4, Integer.parseInt(p[3].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedRestaurants(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO restaurants (id, name, phone, location_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "restaurants.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 4) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setString(3, p[2].trim());
                ps.setInt(4, Integer.parseInt(p[3].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedCouriers(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO couriers (id, name, phone, status, current_location_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "couriers.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 5) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setString(2, p[1].trim());
                ps.setString(3, p[2].trim());
                ps.setString(4, p[3].trim().toUpperCase());
                ps.setInt(5, Integer.parseInt(p[4].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void seedOrders(Connection conn) throws IOException, SQLException {
        String sql = "INSERT INTO orders (id, customer_id, restaurant_id, order_time, priority, status, estimated_prep_minutes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(SEED_DIR + "orders.csv"))) {

            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                if (p.length < 7) continue;

                ps.setInt(1, Integer.parseInt(p[0].trim()));
                ps.setInt(2, Integer.parseInt(p[1].trim()));
                ps.setInt(3, Integer.parseInt(p[2].trim()));
                ps.setString(4, p[3].trim());
                ps.setInt(5, Integer.parseInt(p[4].trim()));
                ps.setString(6, p[5].trim().toUpperCase());
                ps.setInt(7, Integer.parseInt(p[6].trim()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void printSummary(Connection conn) throws SQLException {
        System.out.println("\n=== Summary ===");
        String[] tables = {"locations", "roads", "customers", "restaurants", "couriers", "orders"};
        for (String table : tables) {
            try (var rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM " + table)) {
                if (rs.next()) {
                    System.out.printf("  %-12s: %d rows%n", table, rs.getInt(1));
                }
            }
        }
    }
}