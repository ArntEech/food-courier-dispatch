package com.foodcourier.domain;

public class Restaurant {

    private final String id;
    private final String name;
    private final Location location;

    public Restaurant(
            String id,
            String name,
            Location location
    ) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}