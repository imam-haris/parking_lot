package com.parkinglot.service;

import com.parkinglot.models.Ticket;
import com.parkinglot.models.spot.ParkingSpot;
import com.parkinglot.models.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing ticket lifecycle.
 */
public class TicketService {

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket generateTicket(Vehicle vehicle, ParkingSpot spot) {
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Ticket ticket = new Ticket(ticketId, vehicle, spot, LocalDateTime.now());
        tickets.put(ticketId, ticket);
        return ticket;
    }

    public boolean isValidActiveTicket(String ticketId) {
        if (ticketId == null) return false;
        Ticket ticket = tickets.get(ticketId);
        return ticket != null && ticket.isActive();
    }

    public Ticket getActiveTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null && ticket.isActive()) {
            return ticket;
        }
        return null;
    }

    public void closeTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.markPaid();
        }
    }
}
