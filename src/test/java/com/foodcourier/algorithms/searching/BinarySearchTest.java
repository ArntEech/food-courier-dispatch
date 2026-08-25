package com.foodcourier.algorithms.searching;

import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Location;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTest {

    private static final Comparator<Integer> NATURAL = Comparator.naturalOrder();

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
        Integer[] sorted = {1, 2, 2, 2, 3};
        int index = BinarySearch.search(sorted, 2, NATURAL);
        assertEquals(2, sorted[index]);
    }

    @Test
    void unsortedInputThrowsIllegalArgumentException() {
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

    private static final Location RESTAURANT = new Location("L0", "Campus Bites", 5.6500, -0.1900);

    private static final Comparator<Courier> BY_DISTANCE_FROM_RESTAURANT =
            Comparator.comparingDouble(c -> haversineKm(RESTAURANT, c.getCurrentLocation()));

    private static Courier courierAt(String id, String name, double lat, double lon) {
        Location loc = new Location("L-" + id, name + "'s location", lat, lon);
        return new Courier(id, name, "050000000" + id, CourierStatus.AVAILABLE, loc);
    }

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
        Courier[] sortedByDistance = {
                courierAt("1", "Courier A", 5.6505, -0.1905),
                courierAt("2", "Courier B", 5.6520, -0.1930),
                courierAt("4", "Courier D", 5.6550, -0.1800),
                courierAt("3", "Courier C", 5.6700, -0.1600)
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
                courierAt("3", "Courier C", 5.6700, -0.1600),
                courierAt("1", "Courier A", 5.6505, -0.1905),
        };

        assertThrows(
                IllegalArgumentException.class,
                () -> BinarySearch.search(unsortedByDistance, unsortedByDistance[0], BY_DISTANCE_FROM_RESTAURANT)
        );
    }
}