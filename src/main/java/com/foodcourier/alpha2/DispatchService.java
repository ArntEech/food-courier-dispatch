package com.foodcourier.alpha2;

import com.foodcourier.algorithms.searching.LinearSearch;
import com.foodcourier.domain.Order;
import com.foodcourier.dsa.heap.BinaryHeap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DispatchService {

    private final BinaryHeap heap;
    private final List<Order> activeOrders;

    public DispatchService() {
        heap = new BinaryHeap();
        activeOrders = new ArrayList<>();
    }

    public void insert(Order order) {
        if (order == null) {
            return;
        }

        heap.insert(order);
        activeOrders.add(order);
    }

    public Order dispatchNext() {
        if (heap.isEmpty()) {
            return null;
        }

        Order order = heap.removeMin();
        activeOrders.remove(order);

        return order;
    }

    public Optional<Order> findOrder(String orderId) {
        return LinearSearch.searchById(activeOrders, orderId);
    }
}
