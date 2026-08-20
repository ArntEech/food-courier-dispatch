package com.foodcourier.alpha2;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Location;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DispatchServiceTest {

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

    @Test
    void insertAndFindOrder() {

        DispatchService service = new DispatchService();

        Order order1 = createOrder("O1", Priority.LOW);
        Order order2 = createOrder("O2", Priority.HIGH);

        service.insert(order1);
        service.insert(order2);

        Optional<Order> result = service.findOrder("O2");

        assertTrue(result.isPresent());
        assertEquals("O2", result.get().getId());
    }

    @Test
    void dispatchNextReturnsHighestPriorityAndRemovesIt() {

        DispatchService service = new DispatchService();

        Order low = createOrder("O1", Priority.LOW);
        Order high = createOrder("O2", Priority.HIGH);

        service.insert(low);
        service.insert(high);

        Order dispatched = service.dispatchNext();

        assertNotNull(dispatched);
        assertEquals("O2", dispatched.getId());

        assertTrue(service.findOrder("O2").isEmpty());
        assertTrue(service.findOrder("O1").isPresent());
    }

    @Test
    void dispatchNextReturnsNullWhenEmpty() {

        DispatchService service = new DispatchService();

        assertNull(service.dispatchNext());
    }

    @Test
    void csvOrdersAreDispatchedInPriorityOrder() throws IOException {

        DispatchService service = new DispatchService();

        Path csvPath = Path.of("data/seed/orders.csv");

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {

            String line = reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(",");

                String orderId = columns[0];
                int priorityValue = Integer.parseInt(columns[4]);

                Priority priority = Priority.values()[priorityValue - 1];

                service.insert(createOrder(orderId, priority));
            }
        }

        List<Integer> dispatchedPriorities = new ArrayList<>();

        Order order;

        while ((order = service.dispatchNext()) != null) {
            dispatchedPriorities.add(order.getPriority().getValue());
        }

        List<Integer> expectedPriorities =
                List.of(1, 1, 2, 2, 3, 4);

        assertEquals(expectedPriorities, dispatchedPriorities);
    }
}
