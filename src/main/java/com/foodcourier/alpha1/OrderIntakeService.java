package com.foodcourier.alpha1;

import com.foodcourier.domain.Order;

/**
 * Handles intake of incoming orders and hands them off to the order-processing
 * pipeline.
 *
 * Stub implementation — Issabella will wire in the actual queueing /
 * prioritization algorithms here.
 */
public class OrderIntakeService {

    /**
     * Receives a new order into the intake pipeline.
     *
     * @param order the order being submitted
     */
    public void receiveOrder(Order order) {
        // TODO: Issabella - enqueue/prioritize the order here
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Retrieves the next order to be processed.
     *
     * @return the next order in line
     */
    public Order getNextOrder() {
        // TODO: Issabella - dequeue/select the next order here
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
