package com.foodcourier.algorithms.optimization;

import com.foodcourier.algorithms.optimization.DynamicProgramming.BatchResult;
import com.foodcourier.algorithms.optimization.DynamicProgramming.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class DynamicProgrammingTest {

    @Test
    void emptyOrderList_returnsEmptyResult() {
        BatchResult result = DynamicProgramming.optimizeBatch(new ArrayList<>(), 10);
        assertTrue(result.selectedOrders.isEmpty());
        assertEquals(0, result.totalValue);
        assertEquals(0, result.totalWeight);
    }

    @Test
    void nullOrderList_returnsEmptyResult() {
        BatchResult result = DynamicProgramming.optimizeBatch(null, 10);
        assertTrue(result.selectedOrders.isEmpty());
    }

    @Test
    void zeroCapacity_selectsNothing() {
        List<Order> orders = List.of(new Order("A", 2, 5, 10));
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 0);
        assertTrue(result.selectedOrders.isEmpty());
        assertEquals(0, result.totalValue);
    }

    @Test
    void singleOrderThatFits_isSelected() {
        List<Order> orders = List.of(new Order("A", 3, 10, 5));
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 5);
        assertEquals(1, result.selectedOrders.size());
        assertEquals("A", result.selectedOrders.get(0).id);
        assertEquals(10, result.totalValue);
        assertEquals(3, result.totalWeight);
    }

    @Test
    void singleOrderTooHeavy_isRejected() {
        List<Order> orders = List.of(new Order("A", 20, 10, 5));
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 5);
        assertTrue(result.selectedOrders.isEmpty());
        assertEquals(0, result.totalValue);
    }

    @Test
    void picksHigherValueSubsetOverGreedyChoice() {
        // Greedy-by-value would take A (value 10, weight 9) and miss capacity for anything else.
        // Optimal is B+C = value 12, weight 10.
        List<Order> orders = Arrays.asList(
                new Order("A", 9, 10, 1),
                new Order("B", 5, 6, 1),
                new Order("C", 5, 6, 1)
        );
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 10);
        assertEquals(12, result.totalValue);
        assertEquals(10, result.totalWeight);
        assertEquals(2, result.selectedOrders.size());
    }

    @Test
    void allOrdersFitExactly_selectsAll() {
        List<Order> orders = Arrays.asList(
                new Order("A", 2, 3, 1),
                new Order("B", 3, 4, 1),
                new Order("C", 4, 5, 1)
        );
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 9);
        assertEquals(3, result.selectedOrders.size());
        assertEquals(12, result.totalValue);
        assertEquals(9, result.totalWeight);
    }

    @Test
    void tieBreakStillMaximizesValue() {
        // Two equally-weighted orders, only one fits: DP must pick the higher-value one.
        List<Order> orders = Arrays.asList(
                new Order("Low", 5, 3, 1),
                new Order("High", 5, 8, 1)
        );
        BatchResult result = DynamicProgramming.optimizeBatch(orders, 5);
        assertEquals(1, result.selectedOrders.size());
        assertEquals("High", result.selectedOrders.get(0).id);
        assertEquals(8, result.totalValue);
    }

    @Test
    void timeConstrainedVariant_respectsBothCapacityAndTime() {
        List<Order> orders = Arrays.asList(
                new Order("A", 4, 10, 15), // fits weight, but blows time budget alone if combined wrong
                new Order("B", 3, 7, 5),
                new Order("C", 3, 7, 5)
        );
        // capacity 6, time budget 10 -> B+C (weight 6, time 10, value 14) beats A alone (value 10)
        BatchResult result = DynamicProgramming.optimizeBatchWithTimeConstraint(orders, 6, 10);
        assertEquals(14, result.totalValue);
        assertEquals(6, result.totalWeight);
        assertEquals(2, result.selectedOrders.size());
    }

    @Test
    void timeConstrainedVariant_zeroTimeBudget_selectsNothing() {
        List<Order> orders = List.of(new Order("A", 1, 5, 1));
        BatchResult result = DynamicProgramming.optimizeBatchWithTimeConstraint(orders, 10, 0);
        assertTrue(result.selectedOrders.isEmpty());
    }

    private void assertEquals(int i, int totalValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void assertEquals(String a, String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void assertTrue(boolean empty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
