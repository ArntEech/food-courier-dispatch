package com.foodcourier.algorithms.searching;

import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BinarySearch}.
 *
 * Covers, per Alpha 3's testing requirements (project Table 8):
 *  - normal case (found, not found)
 *  - boundary cases (empty array, single-element array)
 *  - invalid precondition (unsorted input -> required counterexample)
 *  - duplicate keys
 *
 * Two groups of tests:
 *  1. Plain Integer[] tests - fast, isolate the algorithm itself.
 *  2. Courier[]-by-distance tests - now possible because Location carries
 *     latitude/longitude, even though data/seed/locations.csv itself is
 *     still empty (coordinates below are hand-built Legon-area stand-ins,
 *     NOT loaded from the CSV). Swap in real courier/location data once
 *     locations.csv is populated and the team locks the sort key - the
 *     comparator is the only thing that would change.
 */
class BinarySearchTest {

    private static final Comparator<Integer> NATURAL = Comparator.naturalOrder();

    // ==================================================================
    // Group 1: generic Integer[] tests
    // ==================================================================

    @Test
    void findsElementInMiddle() {
        Integer[] sorted = {1, 3, 5, 7, 9};
        assertEquals(2, BinarySearch.search(sorted, 5, NATURAL));
    }

    @Test
    void findsElementAtStartAndEnd() {
        Integer[] sorted = {2, 4, 6, 8, 10};
        assertEquals(0, BinarySearch.search(sorted, 2, NATURAL));
        assertEquals(4, BinarySearch.search(sorted, 10, NATURAL));
    }

    @Test
    void returnsMinusOneWhenNotFound() {
        Integer[] sorted = {1, 3, 5, 7, 9};
        assertEquals(-1, BinarySearch.search(sorted, 4, NATURAL));
    }

    @Test
    void emptyArrayReturnsNotFound() {
        Integer[] empty = {};
        assertEquals(-1, BinarySearch.search(empty, 1, NATURAL));
    }

    @Test
    void singleElementArrayFound() {
        Integer[] single = {42};
        assertEquals(0, BinarySearch.search(single, 42, NATURAL));
    }

    @Test
    void singleElementArrayNotFound() {
        Integer[] single = {42};
        assertEquals(-1, BinarySearch.search(single, 7, NATURAL));
    }

    @Test
    void duplicateKeysReturnsAValidMatchingIndex() {
        // Multiple couriers could plausibly share a rating/zone value, so
        // binary search only needs to guarantee it returns *a* correct
        // index, not necessarily the first or last occurrence.
        Integer[] sorted = {1, 2, 2, 2, 3};
        int index = BinarySearch.search(sorted, 2, NATURAL);
        assertEquals(2, sorted[index]);
    }

    @Test
    void unsortedInputThrowsIllegalArgumentException() {
        // Required "invalid precondition" counterexample: binary search
        // assumes sorted input and must fail loudly, not silently return
        // a wrong answer, when that assumption breaks.
        Integer[] unsorted = {5, 1, 4, 2, 3};

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearch.search(unsorted, 3, NATURAL)
        );
        assertTrue(ex.getMessage().contains("not sorted"));
    }

    @Test
    void nullArrayThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearch.search(null, 1, NATURAL)
        );
    }

    @Test
    void isSortedTrueForSortedArray() {
        Integer[] sorted = {1, 2, 3};
        assertTrue(BinarySearch.isSorted(sorted, NATURAL));
    }

    @Test
    void isSortedFalseForUnsortedArray() {
        Integer[] unsorted = {3, 1, 2};
        assertFalse(BinarySearch.isSorted(unsorted, NATURAL));
    }

    // ==================================================================
    // Group 2: Courier[]-by-distance tests
    //
    // Distance is measured from a fixed reference point (e.g. a restaurant)
    // using the Haversine formula over Location's lat/long. This is a
    // TEST-ONLY comparator - it is not wired into CourierAssignmentService
    // yet since that class (Maa Afia's task) doesn't exist in the repo yet.
    // Coordinates are hand-built Legon-area stand-ins pending real data in
    // data/seed/locations.csv.
    // ==================================================================

    /** Fixed reference point standing in for "the restaurant" - Legon area. */
    private static final Location RESTAURANT = new Location("L0", "Campus Bites", 5.6500, -0.1900);

    private static final Comparator<Courier> BY_DISTANCE_FROM_RESTAURANT =
            Comparator.comparingDouble(c -> haversineKm(RESTAURANT, c.getCurrentLocation()));

    private static Courier courierAt(String id, String name, double lat, double lon) {
        Location loc = new Location("L-" + id, name + "'s location", lat, lon);
        return new Courier(id, name, "050000000" + id, CourierStatus.AVAILABLE, loc);
    }

    /**
     * Haversine great-circle distance in km. Simple, dependency-free stand-in
     * until A4's graph/road-distance logic is available - good enough for
     * "nearest courier" ranking at this stage.
     */
    private static double haversineKm(Location a, Location b) {
        final double earthRadiusKm = 6371.0;
        double dLat = Math.toRadians(b.getLatitude() - a.getLatitude());
        double dLon = Math.toRadians(b.getLongitude() - a.getLongitude());
        double lat1 = Math.toRadians(a.getLatitude());
        double lat2 = Math.toRadians(b.getLatitude());

        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
        return earthRadiusKm * c;
    }

    @Test
    void findsCourierByDistanceFromRestaurant() {
        // Ascending distance from RESTAURANT (5.6500, -0.1900).
        Courier[] sortedByDistance = {
                courierAt("1", "Courier A", 5.6505, -0.1905), // nearest
                courierAt("2", "Courier B", 5.6520, -0.1930),
                courierAt("4", "Courier D", 5.6550, -0.1800),
                courierAt("3", "Courier C", 5.6700, -0.1600)  // farthest
        };

        assertTrue(BinarySearch.isSorted(sortedByDistance, BY_DISTANCE_FROM_RESTAURANT));

        int index = BinarySearch.search(sortedByDistance, sortedByDistance[2], BY_DISTANCE_FROM_RESTAURANT);
        assertEquals("4", sortedByDistance[index].getId());
    }

    @Test
    void courierNotFoundByDistanceReturnsMinusOne() {
        Courier[] sortedByDistance = {
                courierAt("1", "Courier A", 5.6505, -0.1905),
                courierAt("2", "Courier B", 5.6520, -0.1930),
        };
        Courier notInArray = courierAt("9", "Courier Z", 5.9000, -0.5000);

        assertEquals(-1, BinarySearch.search(sortedByDistance, notInArray, BY_DISTANCE_FROM_RESTAURANT));
    }

    @Test
    void unsortedCourierArrayByDistanceThrows() {
        Courier[] unsortedByDistance = {
                courierAt("3", "Courier C", 5.6700, -0.1600), // farthest first - wrong order
                courierAt("1", "Courier A", 5.6505, -0.1905),
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearch.search(unsortedByDistance, unsortedByDistance[0], BY_DISTANCE_FROM_RESTAURANT)
        );
    }
}
