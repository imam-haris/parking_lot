package com.parkinglot.strategy.allocation;

import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.models.ParkingFloor;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

import java.util.Comparator;
import java.util.Optional;

/**
 * Finds the spot closest to the entrance among all floors that can fit the vehicle.
 * Prefers exact spot types first, then falls back to larger spots closest to the ground entrance.
 */
public class NearestEntranceStrategy implements SpotAllocationStrategy {

    @Override
    public ParkingSpot allocate(ParkingLot parkingLot, Vehicle vehicle) throws ParkingFullException {
        // Priority 1: Exact matching type spot with minimum distance
        Optional<ParkingSpot> exactSpot = parkingLot.getFloors().stream()
                .flatMap(f -> f.getAllSpots().stream())
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.getSpotType().name().equals(vehicle.getType().name()))
                .filter(s -> s.canFit(vehicle))
                .min(Comparator.comparingInt(this::calculateEffectiveDistance));

        if (exactSpot.isPresent()) {
            return exactSpot.get();
        }

        // Priority 2: Any compatible spot with minimum distance
        Optional<ParkingSpot> anyFitSpot = parkingLot.getFloors().stream()
                .flatMap(f -> f.getAllSpots().stream())
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.canFit(vehicle))
                .min(Comparator.comparingInt(this::calculateEffectiveDistance));

        if (anyFitSpot.isPresent()) {
            return anyFitSpot.get();
        }

        throw new ParkingFullException("No available parking spot found for vehicle " + vehicle);
    }

    private int calculateEffectiveDistance(ParkingSpot spot) {
        return (spot.getFloorNumber() * 100) + spot.getDistanceFromEntrance();
    }

    @Override
    public String getStrategyName() {
        return "Nearest Entrance Strategy";
    }
}
