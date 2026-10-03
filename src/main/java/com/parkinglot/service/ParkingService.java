package com.parkinglot.service;

import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.exception.InvalidTicketException;
import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.exception.PaymentException;
import com.parkinglot.models.Payment;
import com.parkinglot.models.ParkingLot;
import com.parkinglot.models.Ticket;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;
import com.parkinglot.strategy.allocation.SpotAllocationStrategy;

/**
 * Facade service that orchestrates the complete check-in and check-out flows.
 *
 * Facade Pattern: hides the internal complexity of allocation, ticketing,
 *                 and payment from the client (Main / UI layer).
 * SRP            : coordinates the flow; delegates each concern to its service.
 * DIP            : depends on abstractions (strategies, services).
 */
public class ParkingService {

    private final ParkingLot parkingLot;
    private final SpotAllocationStrategy allocationStrategy;
    private final TicketService ticketService;
    private final PaymentService paymentService;

    public ParkingService(ParkingLot parkingLot,
                          SpotAllocationStrategy allocationStrategy,
                          TicketService ticketService,
                          PaymentService paymentService) {
        this.parkingLot          = parkingLot;
        this.allocationStrategy  = allocationStrategy;
        this.ticketService       = ticketService;
        this.paymentService      = paymentService;
    }

    // ------------------------------------------------------------
    // CHECK-IN
    // ------------------------------------------------------------

    /**
     * Handles vehicle entry:
     *   1. Find a suitable spot via allocation strategy.
     *   2. Park the vehicle in that spot.
     *   3. Generate and return a ticket.
     *
     * @param vehicle the incoming vehicle
     * @return the generated Ticket
     * @throws ParkingFullException if no spot is available for this vehicle type
     */
    public Ticket checkIn(Vehicle vehicle) throws ParkingFullException {
        // 1. Allocate spot
        ParkingSpot spot = allocationStrategy.allocate(parkingLot, vehicle);

        // 2. Park vehicle
        spot.park(vehicle);

        // 3. Issue ticket
        Ticket ticket = ticketService.generateTicket(vehicle, spot);

        System.out.println("  [CHECK-IN]  " + vehicle + " -> Spot: " + spot.getSpotId()
                           + " | Ticket: " + ticket.getTicketId());
        return ticket;
    }

    // ------------------------------------------------------------
    // CHECK-OUT
    // ------------------------------------------------------------

    /**
     * Handles vehicle exit:
     *   1. Validate the ticket.
     *   2. Calculate and process the fee.
     *   3. Free the parking spot.
     *   4. Close the ticket.
     *
     * @param ticketId      the ticket issued at entry
     * @param paymentMethod how the driver wishes to pay
     * @return a Payment receipt
     * @throws InvalidTicketException if the ticket is unknown or already paid
     * @throws PaymentException       if the payment fails
     */
    public Payment checkOut(String ticketId, PaymentMethod paymentMethod)
            throws InvalidTicketException, PaymentException {

        // 1. Validate ticket
        if (!ticketService.isValidActiveTicket(ticketId)) {
            throw new InvalidTicketException(ticketId);
        }
        Ticket ticket = ticketService.getActiveTicket(ticketId);

        // 2. Process payment
        Payment payment = paymentService.processPayment(ticket, paymentMethod);

        // 3. Free the spot
        ParkingSpot spot = ticket.getAllocatedSpot();
        spot.free();

        // 4. Close the ticket
        ticketService.closeTicket(ticketId);

        System.out.println("  [CHECK-OUT] " + ticket.getVehicle() + " | Spot " + spot.getSpotId()
                           + " freed | Fee: INR " + String.format("%.2f", payment.getAmountPaid()));
        return payment;
    }

    // ------------------------------------------------------------
    // AVAILABILITY PREVIEW (for fee estimate before paying)
    // ------------------------------------------------------------

    /**
     * Returns the estimated fee for an active ticket without charging.
     */
    public double estimateFee(String ticketId) throws InvalidTicketException {
        if (!ticketService.isValidActiveTicket(ticketId)) {
            throw new InvalidTicketException(ticketId);
        }
        Ticket ticket = ticketService.getActiveTicket(ticketId);
        return paymentService.calculateFee(ticket);
    }

    // ------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------

    public ParkingLot getParkingLot()                     { return parkingLot; }
    public TicketService getTicketService()                { return ticketService; }
    public PaymentService getPaymentService()              { return paymentService; }
    public SpotAllocationStrategy getAllocationStrategy() { return allocationStrategy; }
}
