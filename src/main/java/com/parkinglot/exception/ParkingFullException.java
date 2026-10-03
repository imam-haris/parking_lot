package com.parkinglot.exception;

/**
 * Thrown when no suitable parking spot is available for the incoming vehicle.
 */
public class ParkingFullException extends ParkingLotException {

    public ParkingFullException(String message) {
        super(message);
    }
}
