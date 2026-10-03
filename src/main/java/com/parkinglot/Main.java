package com.parkinglot;

import com.parkinglot.display.DisplayBoard;
import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.enums.VehicleType;
import com.parkinglot.exception.InvalidTicketException;
import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.exception.PaymentException;
import com.parkinglot.factory.VehicleFactory;
import com.parkinglot.models.Payment;
import com.parkinglot.models.ParkingFloor;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.Ticket;
import com.parkinglot.models.spot.BikeSpot;
import com.parkinglot.models.spot.CarSpot;
import com.parkinglot.models.spot.TruckSpot;
import com.parkinglot.models.vehicle.Vehicle;
import com.parkinglot.service.ParkingService;
import com.parkinglot.service.PaymentService;
import com.parkinglot.service.TicketService;
import com.parkinglot.strategy.allocation.FirstAvailableStrategy;
import com.parkinglot.strategy.allocation.NearestEntranceStrategy;
import com.parkinglot.strategy.pricing.HourlyPricingStrategy;
import com.parkinglot.strategy.pricing.PeakHourPricingStrategy;

/**
 * ===============================================================
 * Parking Lot Management System — SDE-1 LLD Demo
 * ===============================================================
 *
 * Demonstrates:
 *   - Singleton ParkingLot with multi-floor setup
 *   - Vehicle entry/exit via ParkingService facade
 *   - Strategy Pattern: two pricing + two allocation strategies
 *   - Factory Pattern: VehicleFactory, PaymentProcessorFactory
 *   - Fee calculation and multi-method payment (Cash, Card, UPI)
 *   - DisplayBoard for live status
 *   - Edge case / error handling (full lot, invalid ticket, etc.)
 */
public class Main {

    public static void main(String[] args) {

        printBanner();

        // ---------------------------------------------------------
        // 1. SETUP: Build the Parking Lot
        // ---------------------------------------------------------
        ParkingLot lot = buildParkingLot();

        // ---------------------------------------------------------
        // 2. WIRE SERVICES (Strategy Pattern injection)
        // ---------------------------------------------------------
        ParkingService service = new ParkingService(
                lot,
                new NearestEntranceStrategy(),                  // allocation strategy
                new TicketService(),
                new PaymentService(new HourlyPricingStrategy()) // pricing strategy
        );
        DisplayBoard board = new DisplayBoard(service);

        // ---------------------------------------------------------
        // 3. INITIAL STATUS
        // ---------------------------------------------------------
        section("INITIAL STATUS");
        board.showSummary();
        board.showAllFloors();

        // ---------------------------------------------------------
        // 4. SCENARIO 1: Normal vehicle check-ins
        // ---------------------------------------------------------
        section("SCENARIO 1 -- Multiple Vehicle Check-Ins");

        Ticket bikeTicket1  = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE,  "MH12AB1234"));
        Ticket carTicket1   = safeCheckIn(service, VehicleFactory.create(VehicleType.CAR,   "DL01CD5678"));
        Ticket truckTicket1 = safeCheckIn(service, VehicleFactory.create(VehicleType.TRUCK, "KA03EF9012"));
        Ticket bikeTicket2  = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE,  "GJ05GH3456"));
        Ticket carTicket2   = safeCheckIn(service, VehicleFactory.create(VehicleType.CAR,   "TN07IJ7890"));

        System.out.println(bikeTicket1);

        board.showSummary();

        // ---------------------------------------------------------
        // 5. SCENARIO 2: Fee estimation
        // ---------------------------------------------------------
        section("SCENARIO 2 -- Fee Estimation (Before Payment)");
        if (carTicket1 != null) {
            try {
                double estimate = service.estimateFee(carTicket1.getTicketId());
                System.out.println("  Estimated fee for " + carTicket1.getVehicle()
                        + " (ticket " + carTicket1.getTicketId() + "): INR "
                        + String.format("%.2f", estimate));
            } catch (InvalidTicketException e) {
                System.err.println("  [ERROR] " + e.getMessage());
            }
        }

        // ---------------------------------------------------------
        // 6. SCENARIO 3: Check-out with different payment methods
        // ---------------------------------------------------------
        section("SCENARIO 3 -- Vehicle Check-Outs (Mixed Payment Methods)");

        safeCheckOut(service, bikeTicket1,  PaymentMethod.CASH);
        safeCheckOut(service, carTicket1,   PaymentMethod.CREDIT_CARD);
        safeCheckOut(service, truckTicket1, PaymentMethod.UPI);

        board.showSummary();

        // ---------------------------------------------------------
        // 7. SCENARIO 4: Duplicate checkout (invalid ticket)
        // ---------------------------------------------------------
        section("SCENARIO 4 -- Duplicate Checkout (Invalid Ticket)");
        System.out.println("  Attempting to check out an already-paid ticket...");
        safeCheckOut(service, bikeTicket1, PaymentMethod.CASH); // should fail gracefully

        // ---------------------------------------------------------
        // 8. SCENARIO 5: Lot capacity test — fill all BIKE spots
        // ---------------------------------------------------------
        section("SCENARIO 5 -- Fill All Bike Spots (Overflow Test)");

        // Fill remaining bike spots
        Ticket bt3 = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE, "RJ09KL1111"));
        Ticket bt4 = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE, "UP11MN2222"));
        Ticket bt5 = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE, "HR12OP3333"));
        // This should fall through to a CarSpot (bikes can use car spots in NearestEntrance)
        Ticket bt6 = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE, "PB13QR4444"));
        Ticket bt7 = safeCheckIn(service, VehicleFactory.create(VehicleType.BIKE, "MH14ST5555"));

        board.showAvailableSpots();

        // ---------------------------------------------------------
        // 9. SCENARIO 6: Switch to Peak-Hour Pricing + New Strategy
        // ---------------------------------------------------------
        section("SCENARIO 6 -- Strategy Swap: Peak Pricing + First Available");

        ParkingService peakService = new ParkingService(
                lot,
                new FirstAvailableStrategy(),
                new TicketService(),
                new PaymentService(new PeakHourPricingStrategy())
        );

        System.out.println("  New service wired with:");
        System.out.println("    Pricing   : " + peakService.getPaymentService().getPricingStrategyName());
        System.out.println("    Allocation: " + peakService.getAllocationStrategy().getStrategyName());

        // ---------------------------------------------------------
        // 10. SCENARIO 7: Truck filling — Truck spots only
        // ---------------------------------------------------------
        section("SCENARIO 7 -- Truck Check-Ins with Peak Pricing");

        Ticket tt2 = safeCheckIn(peakService, VehicleFactory.create(VehicleType.TRUCK, "MH15UV6666"));
        Ticket tt3 = safeCheckIn(peakService, VehicleFactory.create(VehicleType.TRUCK, "DL16WX7777"));

        if (tt2 != null) {
            Payment p = safeCheckOut(peakService, tt2, PaymentMethod.UPI);
            if (p != null) System.out.println(p);
        }

        // ---------------------------------------------------------
        // 11. SCENARIO 8: Invalid ticket ID
        // ---------------------------------------------------------
        section("SCENARIO 8 -- Unknown Ticket ID");
        System.out.println("  Attempting checkout with a fake ticket ID...");
        try {
            service.checkOut("TKT-FAKEID0", PaymentMethod.CASH);
        } catch (InvalidTicketException | PaymentException e) {
            System.out.println("  [EXPECTED ERROR] Caught exception: " + e.getMessage());
        }

        // ---------------------------------------------------------
        // 12. FINAL STATUS
        // ---------------------------------------------------------
        section("FINAL STATUS");
        board.showSummary();
        board.showAllFloors();

        System.out.println("\nDemo complete.\n");
    }

    // ===========================================================
    // Helper Utilities
    // ===========================================================

    /**
     * Safe wrapper around ParkingService.checkIn() that prints errors instead of crashing.
     */
    private static Ticket safeCheckIn(ParkingService service, Vehicle vehicle) {
        try {
            return service.checkIn(vehicle);
        } catch (ParkingFullException e) {
            System.out.println("  [CHECK-IN FAILED] " + vehicle + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Safe wrapper around ParkingService.checkOut() that prints errors instead of crashing.
     */
    private static Payment safeCheckOut(ParkingService service, Ticket ticket, PaymentMethod method) {
        if (ticket == null) {
            System.out.println("  [WARN] Skipping checkout -- ticket was null (check-in failed).");
            return null;
        }
        try {
            return service.checkOut(ticket.getTicketId(), method);
        } catch (InvalidTicketException | PaymentException e) {
            System.out.println("  [CHECKOUT FAILED] " + e.getMessage());
            return null;
        }
    }

    private static void section(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    private static void printBanner() {
        System.out.println("""
                +============================================================+
                |        PARKING LOT MANAGEMENT SYSTEM - SDE-1 LLD           |
                |    Patterns: Strategy | Factory | Singleton | Facade       |
                |    SOLID: SRP | OCP | LSP | ISP | DIP                     |
                +============================================================+
                """);
    }

    // ===========================================================
    // Parking Lot Setup
    // ===========================================================

    /**
     * Constructs a 3-floor parking lot:
     *   Floor 1 (Ground): 2 Bike spots, 4 Car spots, 2 Truck spots
     *   Floor 2         : 2 Bike spots, 3 Car spots, 1 Truck spot
     *   Floor 3 (Top)   : 0 Bike spots, 3 Car spots, 1 Truck spot
     */
    private static ParkingLot buildParkingLot() {
        ParkingLot lot = ParkingLot.initialize("Central Park Lot", "MG Road, Bengaluru");

        // -- Floor 1 (Ground) ----------------------------------
        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.addSpot(new BikeSpot("F1-B-01",  1,  5));
        floor1.addSpot(new BikeSpot("F1-B-02",  1, 10));
        floor1.addSpot(new CarSpot( "F1-C-01",  1, 15));
        floor1.addSpot(new CarSpot( "F1-C-02",  1, 20));
        floor1.addSpot(new CarSpot( "F1-C-03",  1, 25));
        floor1.addSpot(new CarSpot( "F1-C-04",  1, 30));
        floor1.addSpot(new TruckSpot("F1-T-01", 1, 35));
        floor1.addSpot(new TruckSpot("F1-T-02", 1, 40));
        lot.addFloor(floor1);

        // -- Floor 2 -------------------------------------------
        ParkingFloor floor2 = new ParkingFloor(2);
        floor2.addSpot(new BikeSpot("F2-B-01",  2,  5));
        floor2.addSpot(new BikeSpot("F2-B-02",  2, 10));
        floor2.addSpot(new CarSpot( "F2-C-01",  2, 15));
        floor2.addSpot(new CarSpot( "F2-C-02",  2, 20));
        floor2.addSpot(new CarSpot( "F2-C-03",  2, 25));
        floor2.addSpot(new TruckSpot("F2-T-01", 2, 30));
        lot.addFloor(floor2);

        // -- Floor 3 (Top) -------------------------------------
        ParkingFloor floor3 = new ParkingFloor(3);
        floor3.addSpot(new CarSpot( "F3-C-01",  3, 10));
        floor3.addSpot(new CarSpot( "F3-C-02",  3, 15));
        floor3.addSpot(new CarSpot( "F3-C-03",  3, 20));
        floor3.addSpot(new TruckSpot("F3-T-01", 3, 25));
        lot.addFloor(floor3);

        System.out.println("Parking lot built: " + lot);
        return lot;
    }
}
