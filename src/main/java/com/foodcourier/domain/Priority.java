package com.foodcourier.domain;

public enum Priority {

    HIGH(1),
    MEDIUM(2),
    LOW(3),
    VERY_LOW(4);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}