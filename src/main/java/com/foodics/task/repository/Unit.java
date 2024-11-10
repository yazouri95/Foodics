package com.foodics.task.repository;

public enum Unit {
    GRAM("g"),
    KILOGRAMS("kg"),
    UNKNOWN("unknown");

    private String code;

    Unit(String code) {
        this.code = code;
    }

    public static Unit resolve(String code) {
        return switch (code) {
            case "g" -> GRAM;
            case "kg" -> KILOGRAMS;
            default -> UNKNOWN;
        };
    }

    public double toGrams(double amount) {
        if (this == KILOGRAMS) {
            return amount * 1000;
        } else {
            return amount;
        }
    }

    public double toOrigin(double amount) {
        if (this == GRAM) {
            return amount;
        } else {
            return amount / 1000;
        }
    }
}
