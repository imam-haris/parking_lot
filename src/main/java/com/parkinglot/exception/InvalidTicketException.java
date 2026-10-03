package com.parkinglot.exception;

/**
 * Thrown when an operation is attempted with an invalid or already-used ticket.
 */
public class InvalidTicketException extends ParkingLotException {

    public InvalidTicketException(String ticketId) {
        super("Invalid or expired ticket: " + ticketId);
    }
}
