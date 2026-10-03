package com.parkinglot.exception;

/**
 * Thrown when a payment processing failure occurs.
 */
public class PaymentException extends ParkingLotException {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
