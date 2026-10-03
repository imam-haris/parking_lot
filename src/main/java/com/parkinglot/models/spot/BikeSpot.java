package com.parkinglot.models.spot;

import com.parkinglot.enums.SpotType;
import com.parkinglot.enums.VehicleType;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Dedicated spot for Bikes.
 */
public class BikeSpot extends ParkingSpot {

    public BikeSpot(String spotId, int floorNumber, int distanceFromEntrance) {
        super(spotId, SpotType.BIKE, floorNumber, distanceFromEntrance);
    }

    public BikeSpot(String spotId) {
        super(spotId, SpotType.BIKE);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle != null && vehicle.getType() == VehicleType.BIKE;
    }
}
