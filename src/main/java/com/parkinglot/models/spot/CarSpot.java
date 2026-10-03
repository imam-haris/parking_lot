package com.parkinglot.models.spot;

import com.parkinglot.enums.SpotType;
import com.parkinglot.enums.VehicleType;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Spot that can fit Cars and Bikes.
 */
public class CarSpot extends ParkingSpot {

    public CarSpot(String spotId, int floorNumber, int distanceFromEntrance) {
        super(spotId, SpotType.CAR, floorNumber, distanceFromEntrance);
    }

    public CarSpot(String spotId) {
        super(spotId, SpotType.CAR);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle != null && (vehicle.getType() == VehicleType.CAR || vehicle.getType() == VehicleType.BIKE);
    }
}
