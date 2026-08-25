package com.foodcourier.algorithms.sorting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;

class QuickSortTest {

    @Test
    void sortsOrdersByTimestampAscending() {
        List<Order> orders = new ArrayList<>(List.of(
                order("late", "2026-08-13T12:10:00"),
                order("early", "2026-08-13T12:00:00"),
                order("middle", "2026-08-13T12:05:00")));

        QuickSort.sort(orders);

        assertEquals(List.of("early", "middle", "late"),
                orders.stream().map(Order::getId).toList());
    }

    @Test
    void handlesNullAndSingleItemLists() {
        QuickSort.sort((List<Order>) null);
        List<Order> orders = new ArrayList<>(List.of(order("only", "2026-08-13T12:00:00")));

        QuickSort.sort(orders);

        assertEquals("only", orders.get(0).getId());
    }

    private Order order(String id, String timestamp) {
        return new Order(id, new Customer("customer", "Customer", "", null),
                new Restaurant("restaurant", "Restaurant", null), 25.0, Priority.MEDIUM, OrderStatus.PENDING,
                LocalDateTime.parse(timestamp));
    }
}
