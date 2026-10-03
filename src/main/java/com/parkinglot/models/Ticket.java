package com.parkinglot.models;

import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable receipt issued when a vehicle enters the parking lot.
 */
public class Ticket {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot allocatedSpot;
    private final LocalDateTime entryTime;
    private boolean active;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot allocatedSpot, LocalDateTime entryTime) {
        this.ticketId      = ticketId;
        this.vehicle       = vehicle;
        this.allocatedSpot = allocatedSpot;
        this.entryTime     = entryTime;
        this.active        = true;
    }

    public void markPaid() {
        this.active = false;
    }

    public String getTicketId()            { return ticketId; }
    public Vehicle getVehicle()            { return vehicle; }
    public ParkingSpot getAllocatedSpot()   { return allocatedSpot; }
    public LocalDateTime getEntryTime()    { return entryTime; }
    public boolean isActive()              { return active; }

    @Override
    public String toString() {
        return "\n+--------------------------------------+"
             + "\n|            PARKING TICKET            |"
             + "\n+--------------------------------------+"
             + "\n| Ticket ID : " + padRight(ticketId, 24) + "|"
             + "\n| Vehicle   : " + padRight(vehicle.toString(), 24) + "|"
             + "\n| Spot      : " + padRight(allocatedSpot.getSpotId(), 24) + "|"
             + "\n| Floor     : " + padRight("Floor-" + allocatedSpot.getFloorNumber(), 24) + "|"
             + "\n| Entry     : " + padRight(entryTime.format(FORMATTER), 24) + "|"
             + "\n+--------------------------------------+";
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s);
    }
}
