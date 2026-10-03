package com.parkinglot.models.spot;

import com.parkinglot.enums.SpotType;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Large spot that can fit any vehicle type (Truck, Car, Bike).
 */
public class TruckSpot extends ParkingSpot {

    public TruckSpot(String spotId, int floorNumber, int distanceFromEntrance) {
        super(spotId, SpotType.TRUCK, floorNumber, distanceFromEntrance);
    }

    public TruckSpot(String spotId) {
        super(spotId, SpotType.TRUCK);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle != null;
    }
}
