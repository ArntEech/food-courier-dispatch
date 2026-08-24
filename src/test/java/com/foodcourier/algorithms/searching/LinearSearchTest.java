package com.foodcourier.algorithms.searching;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

class LinearSearchTest {

    @Test
    void findsOrderById() {
        Order target = order("order-2");

        Optional<Order> result = LinearSearch.searchById(List.of(order("order-1"), target), "order-2");

        assertTrue(result.isPresent());
        assertEquals(target, result.get());
    }

    @Test
    void returnsEmptyWhenOrderIsMissingOrIdIsNull() {
        List<Order> orders = List.of(order("order-1"));

        assertTrue(LinearSearch.searchById(orders, "missing").isEmpty());
        assertTrue(LinearSearch.searchById(orders, null).isEmpty());
    }

    private Order order(String id) {
        return new Order(id, new Customer("customer", "Customer", "", null),
                new Restaurant("restaurant", "Restaurant", null), 25.0, Priority.MEDIUM, OrderStatus.PENDING);
    }
}
