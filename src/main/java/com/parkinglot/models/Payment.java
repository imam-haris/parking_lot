package com.parkinglot.models;

import com.parkinglot.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payment receipt representation after checkout is completed.
 */
public class Payment {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String paymentId;
    private final String ticketId;
    private final double amountPaid;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime paymentTime;
    private final String transactionReference;

    public Payment(String paymentId, String ticketId, double amountPaid,
                   PaymentMethod paymentMethod, LocalDateTime paymentTime,
                   String transactionReference) {
        this.paymentId = paymentId;
        this.ticketId = ticketId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.transactionReference = transactionReference;
    }

    public String getPaymentId()            { return paymentId; }
    public String getTicketId()             { return ticketId; }
    public double getAmountPaid()           { return amountPaid; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentTime()   { return paymentTime; }
    public String getTransactionReference() { return transactionReference; }

    @Override
    public String toString() {
        return "\n+--------------------------------------+"
             + "\n|           PAYMENT RECEIPT            |"
             + "\n+--------------------------------------+"
             + "\n| Payment ID : " + padRight(paymentId, 24) + "|"
             + "\n| Ticket ID  : " + padRight(ticketId, 24) + "|"
             + "\n| Method     : " + padRight(paymentMethod.name(), 24) + "|"
             + "\n| Amount     : " + padRight("INR " + String.format("%.2f", amountPaid), 24) + "|"
             + "\n| Reference  : " + padRight(transactionReference, 24) + "|"
             + "\n| Paid At    : " + padRight(paymentTime.format(FORMATTER), 24) + "|"
             + "\n+--------------------------------------+";
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        return String.format("%-" + n + "s", s);
    }
}
