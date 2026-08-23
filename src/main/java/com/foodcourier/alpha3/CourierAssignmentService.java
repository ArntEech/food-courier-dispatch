package com.foodcourier.alpha3;

import com.foodcourier.algorithms.searching.BinarySearch;
import com.foodcourier.algorithms.searching.LinearSearch;
import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Order;
import com.foodcourier.dsa.hashtable.HashMapTable;
import com.foodcourier.dsa.tree.BST;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CourierAssignmentService {

    private final HashMapTable<String, Courier> courierById;
    private final BST<String> courierIdsSorted;
    private final List<Order> activeOrders;

    public CourierAssignmentService(HashMapTable<String, Courier> courierById,
                                     BST<String> courierIdsSorted,
                                     List<Order> activeOrders) {
        this.courierById = courierById;
        this.courierIdsSorted = courierIdsSorted;
        this.activeOrders = activeOrders;
    }

    public Courier findAvailableCourier(Order order) {
        if (order == null) {
            return null;
        }

        Optional<Order> activeMatch = LinearSearch.searchById(activeOrders, order.getId());
        if (activeMatch.isEmpty()) {
            return null;
        }

        String[] sortedIds = courierIdsSorted.inOrder().toArray(new String[0]);

        Courier candidate = null;
        for (String id : sortedIds) {
            Courier c = courierById.get(id);
            if (c != null && c.getStatus() == CourierStatus.AVAILABLE) {
                candidate = c;
                break;
            }
        }
        if (candidate == null) {
            return null;
        }

        int index = BinarySearch.search(sortedIds, candidate.getId(), Comparator.naturalOrder());
        return index == -1 ? null : candidate;
    }

    public void updateStatus(Courier courier, CourierStatus status) {
        courier.setStatus(status);
    }
}
