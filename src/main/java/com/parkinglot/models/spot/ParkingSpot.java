package com.parkinglot.models.spot;

import com.parkinglot.enums.SpotStatus;
import com.parkinglot.enums.SpotType;
import com.parkinglot.models.vehicle.Vehicle;

/**
 * Abstract base class for all parking spots.
 * Implements Template Method / Lifecycle for parking and freeing spots.
 */
public abstract class ParkingSpot {

    private final String spotId;
    private final SpotType spotType;
    private final int floorNumber;
    private final int distanceFromEntrance;
    private SpotStatus status;
    private Vehicle parkedVehicle;

    protected ParkingSpot(String spotId, SpotType spotType, int floorNumber, int distanceFromEntrance) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.floorNumber = floorNumber;
        this.distanceFromEntrance = distanceFromEntrance;
        this.status = SpotStatus.AVAILABLE;
    }

    protected ParkingSpot(String spotId, SpotType spotType) {
        this(spotId, spotType, 1, 0);
    }

    /**
     * Polymorphic method to determine if a vehicle type fits in this spot.
     */
    public abstract boolean canFit(Vehicle vehicle);

    public synchronized void park(Vehicle vehicle) {
        if (!isAvailable()) {
            throw new IllegalStateException("Spot " + spotId + " is already occupied!");
        }
        if (!canFit(vehicle)) {
            throw new IllegalArgumentException("Vehicle " + vehicle + " cannot fit in " + spotType + " spot.");
        }
        this.parkedVehicle = vehicle;
        this.status = SpotStatus.OCCUPIED;
    }

    public synchronized void free() {
        this.parkedVehicle = null;
        this.status = SpotStatus.AVAILABLE;
    }

    public boolean isAvailable()             { return status == SpotStatus.AVAILABLE; }
    public String getSpotId()                { return spotId; }
    public SpotType getSpotType()            { return spotType; }
    public int getFloorNumber()              { return floorNumber; }
    public int getDistanceFromEntrance()     { return distanceFromEntrance; }
    public SpotStatus getStatus()            { return status; }
    public Vehicle getParkedVehicle()        { return parkedVehicle; }

    @Override
    public String toString() {
        return spotId + "[" + spotType + "|" + status + "]";
    }
}
