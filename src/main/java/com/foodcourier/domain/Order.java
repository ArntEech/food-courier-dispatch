package com.foodcourier.domain;

public class Order {

    private final String id;
    private final Customer customer;
    private final Restaurant restaurant;

    private final double value;
    private final Priority priority;

    private OrderStatus status;

    public Order(
            String id,
            Customer customer,
            Restaurant restaurant,
            double value,
            Priority priority,
            OrderStatus status
    ) {
        this.id = id;
        this.customer = customer;
        this.restaurant = restaurant;
        this.value = value;
        this.priority = priority;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public double getValue() {
        return value;
    }

    public Priority getPriority() {
        return priority;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }

    public Object getTimestamp() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTimestamp'");
    }
}