package com.parkinglot.display;

import com.parkinglot.enums.SpotType;
import com.parkinglot.models.ParkingFloor;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.service.ParkingService;

/**
 * Live information display board showing availability across all floors.
 */
public class DisplayBoard {

    private final ParkingLot parkingLot;

    public DisplayBoard(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public DisplayBoard(ParkingService parkingService) {
        this(parkingService.getParkingLot());
    }

    public void showSummary() {
        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|           PARKING LOT STATUS BOARD               |");
        System.out.println("+--------------------------------------------------+");
        System.out.printf("|  Lot          : %-32s |\n", parkingLot.getName());
        System.out.printf("|  Address      : %-32s |\n", parkingLot.getAddress());
        System.out.printf("|  Total Spots  : %-32d |\n", parkingLot.getTotalSpots());
        System.out.printf("|  Available    : %-32d |\n", parkingLot.getTotalAvailableSpots());
        System.out.println("+--------------------------------------------------+");
        System.out.printf("|  Available Bikes  : %-28d |\n", parkingLot.getAvailableSpotsOfType(SpotType.BIKE));
        System.out.printf("|  Available Cars   : %-28d |\n", parkingLot.getAvailableSpotsOfType(SpotType.CAR));
        System.out.printf("|  Available Trucks : %-28d |\n", parkingLot.getAvailableSpotsOfType(SpotType.TRUCK));
        System.out.println("+--------------------------------------------------+\n");
    }

    public void showAllFloors() {
        System.out.println("--- FLOOR-BY-FLOOR STATUS ---");
        for (ParkingFloor floor : parkingLot.getFloors()) {
            System.out.printf("\n[Floor %d] - Available: %d / %d spots\n",
                    floor.getFloorNumber(), floor.countAvailableSpots(), floor.countTotalSpots());
            for (ParkingSpot spot : floor.getAllSpots()) {
                String statusIcon = spot.isAvailable() ? "[AVAILABLE]" : "[OCCUPIED ]";
                String vehicleInfo = spot.isAvailable() ? "" : " -> " + spot.getParkedVehicle();
                System.out.printf("  %s %-10s [%-5s]%s\n",
                        statusIcon, spot.getSpotId(), spot.getSpotType(), vehicleInfo);
            }
        }
        System.out.println();
    }

    public void showAvailableSpots() {
        System.out.println("Available Spots Summary -> Bike: " + parkingLot.getAvailableSpotsOfType(SpotType.BIKE)
                + " | Car: " + parkingLot.getAvailableSpotsOfType(SpotType.CAR)
                + " | Truck: " + parkingLot.getAvailableSpotsOfType(SpotType.TRUCK));
    }
}
