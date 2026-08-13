package com.foodcourier.domain;

public class Courier {

    private final String id;
    private final String name;
    private final String phone;

    private CourierStatus status;
    private Location currentLocation;

    public Courier(
            String id,
            String name,
            String phone,
            CourierStatus status,
            Location currentLocation
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.currentLocation = currentLocation;
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

    public CourierStatus getStatus() {
        return status;
    }

    public void setStatus(CourierStatus status) {
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}