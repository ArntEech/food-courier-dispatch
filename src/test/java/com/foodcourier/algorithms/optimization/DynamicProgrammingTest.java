package com.foodcourier.algorithms.optimization;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynamicProgrammingTest {

    @Test
    void selectsOptimalSubsetUnderCapacity() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 2, 10));
        items.add(new DynamicProgramming.Item("O2", 3, 15));
        items.add(new DynamicProgramming.Item("O3", 5, 25));
        items.add(new DynamicProgramming.Item("O4", 7, 30));

        List<DynamicProgramming.Item> result =
                DynamicProgramming.knapsack(items, 10);

        assertEquals(50, DynamicProgramming.totalValue(result));
        assertEquals(10, DynamicProgramming.totalWeight(result));
    }

    @Test
    void returnsEmptyListWhenNoItems() {
        List<DynamicProgramming.Item> result =
                DynamicProgramming.knapsack(new ArrayList<>(), 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyListWhenZeroCapacity() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 5, 10));

        List<DynamicProgramming.Item> result =
                DynamicProgramming.knapsack(items, 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void handlesSingleItemThatFits() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 3, 15));

        List<DynamicProgramming.Item> result =
                DynamicProgramming.knapsack(items, 5);

        assertEquals(1, result.size());
        assertEquals("O1", result.get(0).id);
    }

    @Test
    void handlesSingleItemTooHeavy() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 10, 15));

        List<DynamicProgramming.Item> result =
                DynamicProgramming.knapsack(items, 5);

        assertTrue(result.isEmpty());
    }

    @Test
    void maxValueReturnsOptimalValue() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 2, 10));
        items.add(new DynamicProgramming.Item("O2", 3, 15));
        items.add(new DynamicProgramming.Item("O3", 5, 25));

        int maxValue = DynamicProgramming.maxValue(items, 7);

        assertEquals(35, maxValue);
    }

    @Test
    void totalValueAndWeightCalculateCorrectly() {
        List<DynamicProgramming.Item> items = new ArrayList<>();
        items.add(new DynamicProgramming.Item("O1", 3, 10));
        items.add(new DynamicProgramming.Item("O2", 5, 20));

        assertEquals(30, DynamicProgramming.totalValue(items));
        assertEquals(8, DynamicProgramming.totalWeight(items));
    }
}
