package com.parkinglot.strategy.allocation;

import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.models.ParkingFloor;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Scans floors sequentially from 1 to N and picks the first spot that fits the vehicle.
 */
public class FirstAvailableStrategy implements SpotAllocationStrategy {

    @Override
    public ParkingSpot allocate(ParkingLot parkingLot, Vehicle vehicle) throws ParkingFullException {
        for (ParkingFloor floor : parkingLot.getFloors()) {
            for (ParkingSpot spot : floor.getAllSpots()) {
                if (spot.isAvailable() && spot.canFit(vehicle)) {
                    return spot;
                }
            }
        }
        throw new ParkingFullException("No available parking spot found for vehicle " + vehicle);
    }

    @Override
    public String getStrategyName() {
        return "First Available Strategy";
    }
}
