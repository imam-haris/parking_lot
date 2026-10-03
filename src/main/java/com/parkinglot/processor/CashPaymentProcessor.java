package com.parkinglot.processor;

import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.exception.PaymentException;
import com.parkinglot.models.Payment;
import com.parkinglot.models.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public class CashPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment process(Ticket ticket, double amount) throws PaymentException {
        if (amount < 0) {
            throw new PaymentException("Payment amount cannot be negative");
        }
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String ref = "CASH-REC-" + System.currentTimeMillis() % 100000;
        System.out.println("  [CASH] Collected INR " + String.format("%.2f", amount) + " in cash.");
        return new Payment(paymentId, ticket.getTicketId(), amount, PaymentMethod.CASH, LocalDateTime.now(), ref);
    }
}
