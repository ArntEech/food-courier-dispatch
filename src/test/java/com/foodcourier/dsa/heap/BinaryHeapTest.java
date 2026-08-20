package com.foodcourier.dsa.heap;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

public class BinaryHeapTest {

    private static Order createOrder(String id, Priority priority) {

        Location location =
                new Location("L1", "Accra", 5.6037, -0.1870);

        Customer customer =
                new Customer(
                        "C1",
                        "Test Customer",
                        "0240000000",
                        location
                );

        Restaurant restaurant =
                new Restaurant(
                        "R1",
                        "Test Restaurant",
                        location
                );

        return new Order(
                id,
                customer,
                restaurant,
                50.0,
                priority,
                OrderStatus.RECEIVED
        );
    }

    public static void main(String[] args) {

        testInsertAndMin();

        testRemoveMin();

        testSize();

        testEmptyHeap();

        System.out.println("All BinaryHeap tests passed.");
    }

    private static void testInsertAndMin() {

        BinaryHeap heap = new BinaryHeap();

        Order low = createOrder("O1", Priority.LOW);
        Order high = createOrder("O2", Priority.HIGH);
        Order medium = createOrder("O3", Priority.MEDIUM);

        heap.insert(low);
        heap.insert(high);
        heap.insert(medium);

        if (!heap.min().getId().equals("O2")) {
            throw new AssertionError(
                    "Expected O2 to be the minimum, but got "
                            + heap.min().getId()
            );
        }

        System.out.println("testInsertAndMin passed");
    }

    private static void testRemoveMin() {

        BinaryHeap heap = new BinaryHeap();

        heap.insert(createOrder("O1", Priority.LOW));
        heap.insert(createOrder("O2", Priority.HIGH));
        heap.insert(createOrder("O3", Priority.MEDIUM));

        Order first = heap.removeMin();

        if (!first.getId().equals("O2")) {
            throw new AssertionError("Expected O2 first");
        }

        Order second = heap.removeMin();

        if (!second.getId().equals("O3")) {
            throw new AssertionError("Expected O3 second");
        }

        Order third = heap.removeMin();

        if (!third.getId().equals("O1")) {
            throw new AssertionError("Expected O1 third");
        }

        System.out.println("testRemoveMin passed");
    }

    private static void testSize() {

        BinaryHeap heap = new BinaryHeap();

        if (heap.size() != 0) {
            throw new AssertionError("New heap should have size 0");
        }

        heap.insert(createOrder("O1", Priority.HIGH));

        if (heap.size() != 1) {
            throw new AssertionError("Heap should have size 1");
        }

        heap.insert(createOrder("O2", Priority.LOW));

        if (heap.size() != 2) {
            throw new AssertionError("Heap should have size 2");
        }

        System.out.println("testSize passed");
    }

    private static void testEmptyHeap() {

        BinaryHeap heap = new BinaryHeap();

        if (!heap.isEmpty()) {
            throw new AssertionError("New heap should be empty");
        }

        if (heap.removeMin() != null) {
            throw new AssertionError(
                    "removeMin() should return null on an empty heap"
            );
        }

        System.out.println("testEmptyHeap passed");
    }
}


