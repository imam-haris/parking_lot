package com.parkinglot.models;

import com.parkinglot.enums.SpotType;
import com.parkinglot.models.spot.ParkingSpot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton representing the entire parking lot facility.
 *
 * Acts as the aggregate root — all floors and their spots are accessed through it.
 *
 * Singleton pattern:  Only one ParkingLot exists per JVM instance.
 * SRP              :  Holds structural data (floors/spots). Logic delegated to services.
 */
public class ParkingLot {

    // ---- Singleton ----
    private static volatile ParkingLot instance;

    private final String name;
    private final String address;
    private final List<ParkingFloor> floors;

    private ParkingLot(String name, String address) {
        this.name    = name;
        this.address = address;
        this.floors  = new ArrayList<>();
    }

    /**
     * Thread-safe double-checked locking singleton initializer.
     * Must be called once before {@link #getInstance()}.
     */
    public static ParkingLot initialize(String name, String address) {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot(name, address);
                }
            }
        }
        return instance;
    }

    public static ParkingLot getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "ParkingLot not initialized. Call ParkingLot.initialize() first.");
        }
        return instance;
    }

    // ---- Floor management ----

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public List<ParkingFloor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    public ParkingFloor getFloor(int floorNumber) {
        return floors.stream()
                .filter(f -> f.getFloorNumber() == floorNumber)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Floor " + floorNumber + " does not exist."));
    }

    // ---- Aggregate availability across all floors ----

    public List<ParkingSpot> getAllAvailableSpots() {
        return floors.stream()
                .flatMap(f -> f.getAllSpots().stream())
                .filter(ParkingSpot::isAvailable)
                .collect(Collectors.toList());
    }

    public long getTotalAvailableSpots() {
        return floors.stream().mapToLong(ParkingFloor::countAvailableSpots).sum();
    }

    public long getAvailableSpotsOfType(SpotType type) {
        return floors.stream()
                .mapToLong(f -> f.countAvailableSpotsOfType(type))
                .sum();
    }

    public long getTotalSpots() {
        return floors.stream().mapToLong(ParkingFloor::countTotalSpots).sum();
    }

    // ---- Getters ----

    public String getName()    { return name; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return "ParkingLot{name='" + name + "', floors=" + floors.size()
               + ", totalSpots=" + getTotalSpots()
               + ", available=" + getTotalAvailableSpots() + "}";
    }
}
