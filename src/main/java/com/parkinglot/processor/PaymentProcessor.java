package com.parkinglot.processor;

import com.parkinglot.exception.PaymentException;
import com.parkinglot.models.Payment;
import com.parkinglot.models.Ticket;

/**
 * Strategy interface for processing payments.
 */
public interface PaymentProcessor {

    Payment process(Ticket ticket, double amount) throws PaymentException;
}
