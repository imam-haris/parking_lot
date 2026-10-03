package com.parkinglot.factory;

import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.processor.CashPaymentProcessor;
import com.parkinglot.processor.CreditCardPaymentProcessor;
import com.parkinglot.processor.PaymentProcessor;
import com.parkinglot.processor.UpiPaymentProcessor;

/**
 * Factory for creating appropriate PaymentProcessor instances.
 */
public class PaymentProcessorFactory {

    private PaymentProcessorFactory() {}

    public static PaymentProcessor getProcessor(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        switch (method) {
            case CASH:
                return new CashPaymentProcessor();
            case CREDIT_CARD:
                return new CreditCardPaymentProcessor();
            case UPI:
                return new UpiPaymentProcessor();
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }
}
