package com.foodcourier.domain;

public class Delivery {

    private final String id;
    private final Order order;

    private Courier courier;
    private double distance;
    private int estimatedTimeMinutes;

    public Delivery(
            String id,
            Order order,
            double distance,
            int estimatedTimeMinutes
    ) {
        this.id = id;
        this.order = order;
        this.distance = distance;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public double getDistance() {
        return distance;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id='" + id + '\'' +
                ", order=" + order.getId() +
                '}';
    }
}