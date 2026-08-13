package com.foodcourier.domain;

public class Road {

    private final Location from;
    private final Location to;
    private final double distance;

    public Road(
            Location from,
            Location to,
            double distance
    ) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return from.getName() +
                " -> " +
                to.getName() +
                " (" +
                distance +
                " km)";
    }
}