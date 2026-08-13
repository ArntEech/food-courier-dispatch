package com.foodcourier.domain;

public class Customer {

    private final String id;
    private final String name;
    private final String phone;
    private final Location location;

    public Customer(
            String id,
            String name,
            String phone,
            Location location
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}