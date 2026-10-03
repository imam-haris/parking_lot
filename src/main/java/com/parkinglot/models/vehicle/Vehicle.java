package com.parkinglot.models.vehicle;

import com.parkinglot.enums.VehicleType;

// Abstract base — holds identity and type only
public abstract class Vehicle {

    private final String licensePlate;
    private final VehicleType type;

    protected Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate.toUpperCase().trim();
        this.type = type;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType()    { return type; }

    @Override
    public String toString() { return type + "(" + licensePlate + ")"; }
}
