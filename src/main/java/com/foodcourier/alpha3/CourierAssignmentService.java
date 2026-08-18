package com.foodcourier.alpha3;

import com.foodcourier.domain.Courier;
import com.foodcourier.domain.CourierStatus;
import com.foodcourier.domain.Order;

public class CourierAssignmentService {

    public Courier findAvailableCourier(Order order) {
        return null;
    }

    public void updateStatus(Courier courier, CourierStatus status) {
        courier.setStatus(status);
    }
}
