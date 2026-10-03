package com.parkinglot.models;

import com.parkinglot.enums.SpotType;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents one floor of the parking lot.
 *
 * SRP - Manages the collection of spots on a single floor.
 *       Does NOT allocate spots — that's the allocation strategy's job.
 */
public class ParkingFloor {

    private final int floorNumber;
    private final String floorName;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.floorName   = "Floor-" + floorNumber;
        this.spots        = new ArrayList<>();
    }

    // ---- Spot management ----

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    /**
     * Returns all spots on this floor (unmodifiable view).
     */
    public List<ParkingSpot> getAllSpots() {
        return Collections.unmodifiableList(spots);
    }

    /**
     * Returns all available spots that can physically fit the given vehicle.
     */
    public List<ParkingSpot> getAvailableSpotsFor(Vehicle vehicle) {
        return spots.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.canFit(vehicle))
                .collect(Collectors.toList());
    }

    /**
     * Returns all available spots of a specific type.
     */
    public List<ParkingSpot> getAvailableSpotsByType(SpotType type) {
        return spots.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.getSpotType() == type)
                .collect(Collectors.toList());
    }

    // ---- Availability counts ----

    public long countAvailableSpots() {
        return spots.stream().filter(ParkingSpot::isAvailable).count();
    }

    public long countAvailableSpotsOfType(SpotType type) {
        return spots.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.getSpotType() == type)
                .count();
    }

    public long countTotalSpots() {
        return spots.size();
    }

    // ---- Getters ----

    public int getFloorNumber() { return floorNumber; }
    public String getFloorName() { return floorName; }

    @Override
    public String toString() {
        return floorName + " [total=" + spots.size() + ", available=" + countAvailableSpots() + "]";
    }
}
