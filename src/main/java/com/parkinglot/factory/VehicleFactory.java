package com.parkinglot.factory;

import com.parkinglot.enums.VehicleType;
import com.parkinglot.models.vehicle.Bike;
import com.parkinglot.models.vehicle.Car;
import com.parkinglot.models.vehicle.Truck;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Factory for creating Vehicle instances based on VehicleType.
 */
public class VehicleFactory {

    private VehicleFactory() {}

    public static Vehicle create(VehicleType type, String licensePlate) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }

        switch (type) {
            case BIKE:
                return new Bike(licensePlate);
            case CAR:
                return new Car(licensePlate);
            case TRUCK:
                return new Truck(licensePlate);
            default:
                throw new IllegalArgumentException("Unsupported vehicle type: " + type);
        }
    }
}
