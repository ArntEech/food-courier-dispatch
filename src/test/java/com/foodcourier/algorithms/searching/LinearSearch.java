package com.foodcourier.algorithms.searching;

import java.util.List;
import java.util.Optional;

import com.foodcourier.domain.Order;

public class LinearSearch {

    /**
     * Searches a list of orders sequentially by Order ID.
     * Shared across Alpha 1 and Alpha 3.
     */
    public static Optional<Order> searchById(List<Order> orders, String orderId) {
        if (orders == null || orderId == null) {
            return Optional.empty();
        }

        for (Order order : orders) {
            if (orderId.equals(order.getId())) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }
}