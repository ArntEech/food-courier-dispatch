package com.foodcourier.alpha2;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

import java.util.Optional;

public class DispatchServiceTest {

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

        testInsertAndFindOrder();
        testDispatchNextAndFindOrder();

        System.out.println("All DispatchService tests passed.");
    }

    private static void testInsertAndFindOrder() {

        DispatchService service = new DispatchService();

        Order order1 = createOrder("O1", Priority.LOW);
        Order order2 = createOrder("O2", Priority.HIGH);

        service.insert(order1);
        service.insert(order2);

        Optional<Order> result = service.findOrder("O2");

        if (result.isEmpty()) {
            throw new AssertionError("Expected O2 to be found");
        }

        if (!result.get().getId().equals("O2")) {
            throw new AssertionError("Expected to find O2");
        }

        System.out.println("testInsertAndFindOrder passed");
    }

    private static void testDispatchNextAndFindOrder() {

        DispatchService service = new DispatchService();

        Order low = createOrder("O1", Priority.LOW);
        Order high = createOrder("O2", Priority.HIGH);

        service.insert(low);
        service.insert(high);

        Order dispatched = service.dispatchNext();

        if (dispatched == null) {
            throw new AssertionError("Expected an order to be dispatched");
        }

        if (!dispatched.getId().equals("O2")) {
            throw new AssertionError(
                    "Expected O2 to be dispatched first"
            );
        }

        Optional<Order> result = service.findOrder("O2");

        if (result.isPresent()) {
            throw new AssertionError(
                    "O2 should not be found after dispatch"
            );
        }

        Optional<Order> remaining = service.findOrder("O1");

        if (remaining.isEmpty()) {
            throw new AssertionError(
                    "O1 should still be in the dispatch queue"
            );
        }

        System.out.println("testDispatchNextAndFindOrder passed");
    }
}
