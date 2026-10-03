package com.parkinglot.exception;

/**
 * Base checked exception for all domain errors in the parking lot system.
 */
public class ParkingLotException extends Exception {
    public ParkingLotException(String message) {
        super(message);
    }

    public ParkingLotException(String message, Throwable cause) {
        super(message, cause);
    }
}
