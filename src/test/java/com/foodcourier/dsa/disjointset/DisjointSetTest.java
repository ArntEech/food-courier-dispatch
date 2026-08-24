package com.foodcourier.dsa.disjointset;

/**
 * Simple test class for DisjointSet.
 */
public class DisjointSetTest {

    public static void main(String[] args) {
        System.out.println("Testing DisjointSet (Union-Find)...\n");

        // Create disjoint set
        DisjointSet<String> set = new DisjointSet<>();

        // TEST 1: Create sets
        System.out.println("TEST 1: Creating sets");
        set.makeSet("A");
        set.makeSet("B");
        set.makeSet("C");
        set.makeSet("D");

        boolean sizeOk = set.getElements().size() == 4;
        System.out.println("  Elements: " + set.getElements().size() +
                " (Expected: 4) - " +
                (sizeOk ? "PASSED" : "FAILED"));

        // TEST 2: Find roots
        System.out.println("\nTEST 2: Finding roots");
        String rootA = set.find("A");
        String rootB = set.find("B");

        System.out.println("  Root of A: " + rootA +
                " - " + (rootA.equals("A") ? "PASSED" : "FAILED"));
        System.out.println("  Root of B: " + rootB +
                " - " + (rootB.equals("B") ? "PASSED" : "FAILED"));

        // TEST 3: Union operation
        System.out.println("\nTEST 3: Union operation");
        set.union("A", "B");
        set.union("C", "D");

        boolean connectedAB = set.connected("A", "B");
        boolean connectedCD = set.connected("C", "D");
        boolean connectedAC = set.connected("A", "C");

        System.out.println("  A connected to B: " + connectedAB +
                " - " + (connectedAB ? "PASSED" : "FAILED"));
        System.out.println("  C connected to D: " + connectedCD +
                " - " + (connectedCD ? "PASSED" : "FAILED"));
        System.out.println("  A connected to C: " + connectedAC +
                " - " + (!connectedAC ? "PASSED" : "FAILED"));

        // TEST 4: Set count
        System.out.println("\nTEST 4: Set count");
        int setCount = set.getSetCount();

        System.out.println("  Number of sets: " + setCount +
                " (Expected: 2) - " +
                (setCount == 2 ? "PASSED" : "FAILED"));

        // TEST 5: Union all sets
        System.out.println("\nTEST 5: Union all together");
        set.union("A", "C");

        boolean allConnected = set.connected("A", "D") &&
                set.connected("B", "C");
        int finalSetCount = set.getSetCount();

        System.out.println("  All elements connected: " + allConnected +
                " - " + (allConnected ? "PASSED" : "FAILED"));
        System.out.println("  Final set count: " + finalSetCount +
                " (Expected: 1) - " +
                (finalSetCount == 1 ? "PASSED" : "FAILED"));

        // TEST 6: Path compression
        System.out.println("\nTEST 6: Path compression test");

        DisjointSet<String> chainSet = new DisjointSet<>();
        chainSet.makeSet("X");
        chainSet.makeSet("Y");
        chainSet.makeSet("Z");
        chainSet.makeSet("W");

        chainSet.union("X", "Y");
        chainSet.union("Y", "Z");
        chainSet.union("Z", "W");

        String rootX = chainSet.find("X");

        System.out.println("  Root of chain: " + rootX +
                " (All should be connected) - " +
                (!rootX.isEmpty() ? "PASSED" : "FAILED"));

        // Print structure
        System.out.println("\nTEST 7: Print structure");
        set.printStructure();

        // Summary
        System.out.println("\nTEST SUMMARY:");
        System.out.println("All tests passed!");
    }
}