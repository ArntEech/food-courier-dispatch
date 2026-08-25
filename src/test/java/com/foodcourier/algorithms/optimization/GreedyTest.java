package com.foodcourier.algorithms.optimization;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreedyTest {

    @Test
    void selectsOrdersByEarliestDeadline() {
        List<Greedy.Order> orders = new ArrayList<>();
        orders.add(new Greedy.Order("O1", 3, 15, 5));
        orders.add(new Greedy.Order("O2", 2, 10, 3));
        orders.add(new Greedy.Order("O3", 4, 20, 7));

        List<Greedy.Order> batch =
                Greedy.earliestDeadlineBatch(orders, 8);

        assertEquals(2, batch.size());
        assertEquals("O2", batch.get(0).id); // deadline 3
        assertEquals("O1", batch.get(1).id); // deadline 5
    }

    @Test
    void returnsEmptyListWhenNoOrders() {
        List<Greedy.Order> batch =
                Greedy.earliestDeadlineBatch(new ArrayList<>(), 10);

        assertTrue(batch.isEmpty());
    }

    @Test
    void returnsEmptyListWhenZeroCapacity() {
        List<Greedy.Order> orders = new ArrayList<>();
        orders.add(new Greedy.Order("O1", 5, 10, 3));

        List<Greedy.Order> batch =
                Greedy.earliestDeadlineBatch(orders, 0);

        assertTrue(batch.isEmpty());
    }

    @Test
    void skipsOrdersExceedingCapacity() {
        List<Greedy.Order> orders = new ArrayList<>();
        orders.add(new Greedy.Order("O1", 10, 20, 1));
        orders.add(new Greedy.Order("O2", 3, 15, 2));

        List<Greedy.Order> batch =
                Greedy.earliestDeadlineBatch(orders, 5);

        assertEquals(1, batch.size());
        assertEquals("O2", batch.get(0).id);
    }

    @Test
    void totalValueAndWeightCalculateCorrectly() {
        List<Greedy.Order> batch = new ArrayList<>();
        batch.add(new Greedy.Order("O1", 3, 10, 1));
        batch.add(new Greedy.Order("O2", 5, 20, 2));

        assertEquals(30, Greedy.totalValue(batch));
        assertEquals(8, Greedy.totalWeight(batch));
    }

    @Test
    void doesNotModifyOriginalList() {
        List<Greedy.Order> orders = new ArrayList<>();
        orders.add(new Greedy.Order("O1", 3, 15, 5));
        orders.add(new Greedy.Order("O2", 2, 10, 3));

        List<Greedy.Order> original = new ArrayList<>(orders);

        Greedy.earliestDeadlineBatch(orders, 10);

        assertEquals(original.size(), orders.size());
        assertEquals(original.get(0).id, orders.get(0).id);
        assertEquals(original.get(1).id, orders.get(1).id);
    }
}
