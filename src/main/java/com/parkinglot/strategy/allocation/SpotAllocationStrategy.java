package com.parkinglot.strategy.allocation;

import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Strategy interface for allocating an available spot to an incoming vehicle.
 */
public interface SpotAllocationStrategy {

    ParkingSpot allocate(ParkingLot parkingLot, Vehicle vehicle) throws ParkingFullException;

    String getStrategyName();
}
