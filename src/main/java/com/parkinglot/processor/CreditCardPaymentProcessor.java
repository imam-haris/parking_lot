package com.parkinglot.processor;

import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.exception.PaymentException;
import com.parkinglot.models.Payment;
import com.parkinglot.models.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreditCardPaymentProcessor implements PaymentProcessor {

    @Override
    public Payment process(Ticket ticket, double amount) throws PaymentException {
        if (amount < 0) {
            throw new PaymentException("Payment amount cannot be negative");
        }
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String ref = "AUTH-CC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println("  [CARD] Payment authorised. Transaction complete. Ref: " + ref);
        return new Payment(paymentId, ticket.getTicketId(), amount, PaymentMethod.CREDIT_CARD, LocalDateTime.now(), ref);
    }
}
