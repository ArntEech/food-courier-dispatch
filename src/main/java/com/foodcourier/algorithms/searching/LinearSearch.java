package com.foodcourier.algorithms.searching;

import java.util.List;
import java.util.Optional;

import com.foodcourier.domain.Order;

public class LinearSearch {

    public static Optional<Order> searchById(List<Order> activeOrders, String orderId) {
        if (activeOrders == null || orderId == null) {
            return Optional.empty();
        }

        for (Order order : activeOrders) {
            if (order != null && orderId.equals(order.getId())) {
                return Optional.of(order);
            }
        }

        return Optional.empty();
    }
    
}
