package com.foodcourier.alpha1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.foodcourier.algorithms.searching.LinearSearch;
import com.foodcourier.algorithms.sorting.QuickSort;
import com.foodcourier.domain.Customer;
import com.foodcourier.domain.Order;
import com.foodcourier.domain.OrderStatus;
import com.foodcourier.domain.Priority;
import com.foodcourier.domain.Restaurant;
import com.foodcourier.dsa.queue.ArrayQueue;

/**
 * Handles intake of incoming orders and hands them off to the order-processing
 * pipeline.
 */
public class OrderIntakeService {

    private final ArrayQueue<Order> orderQueue = new ArrayQueue<>();

    /**
     * Receives a new order into the intake pipeline.
     *
     * @param order the order being submitted
     */
    public void receiveOrder(Order order) {
        if (order != null) {
            orderQueue.enqueue(order);
        }
    }

    /**
     * Retrieves the next order to be processed (FIFO).
     *
     * @return the next order in line, or null if the queue is empty
     */
    public Order getNextOrder() {
        return orderQueue.isEmpty() ? null : orderQueue.dequeue();
    }

    /**
     * Loads orders into the queue in the order supplied by the seed file.
     */
    public void loadSeedData(List<Order> orders) {
        orderQueue.clear();
        if (orders != null) {
            for (Order order : orders) {
                receiveOrder(order);
            }
        }
    }

    /**
     * Loads the order rows from a CSV seed file into the FIFO queue.
     */
    public void loadSeedData(Path csvPath) throws IOException {
        List<Order> orders = new ArrayList<>();
        try (Stream<String> lines = Files.lines(csvPath)) {
            lines.skip(1)
                    .filter(line -> !line.isBlank())
                    .map(this::parseSeedOrder)
                    .forEach(orders::add);
        }
        loadSeedData(orders);
    }

    private Order parseSeedOrder(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length < 6) {
            throw new IllegalArgumentException("Invalid order seed row: " + line);
        }

        String customerId = columns[1].trim();
        String restaurantId = columns[2].trim();
        Customer customer = new Customer(customerId, "Customer " + customerId, "", null);
        Restaurant restaurant = new Restaurant(restaurantId, "Restaurant " + restaurantId, null);
        return new Order(
                columns[0].trim(),
                customer,
                restaurant,
                0.0,
                parsePriority(columns[4].trim()),
                OrderStatus.valueOf(columns[5].trim().toUpperCase()),
                LocalDateTime.parse(columns[3].trim())
        );
    }

    private Priority parsePriority(String value) {
        int numericPriority = Integer.parseInt(value);
        for (Priority priority : Priority.values()) {
            if (priority.getValue() == numericPriority) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown order priority: " + value);
    }

    /**
     * Searches active orders in the queue by Order ID.
     *
     * @param orderId ID to locate
     * @return Optional containing the order if found
     */
    public Optional<Order> findOrderById(String orderId) {
        return LinearSearch.searchById(orderQueue.toList(), orderId);
    }

    public List<Order> getOrdersSortedByTimestamp() {
        List<Order> orders = new ArrayList<>(orderQueue.toList());
        QuickSort.sort(orders);
        return orders;
    }

    public int getQueueSize() {
        return orderQueue.size();
    }
}
