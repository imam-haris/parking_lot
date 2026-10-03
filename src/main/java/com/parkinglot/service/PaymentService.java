package com.parkinglot.service;

import com.parkinglot.enums.PaymentMethod;
import com.parkinglot.exception.PaymentException;
import com.parkinglot.factory.PaymentProcessorFactory;
import com.parkinglot.models.Payment;
import com.parkinglot.models.Ticket;
import com.parkinglot.processor.PaymentProcessor;
import com.parkinglot.strategy.pricing.HourlyPricingStrategy;
import com.parkinglot.strategy.pricing.PricingStrategy;

/**
 * Service responsible for calculating fees and processing transactions.
 */
public class PaymentService {

    private final PricingStrategy pricingStrategy;

    public PaymentService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy != null ? pricingStrategy : new HourlyPricingStrategy();
    }

    public PaymentService() {
        this(new HourlyPricingStrategy());
    }

    public double calculateFee(Ticket ticket) {
        return pricingStrategy.calculateFee(ticket);
    }

    public Payment processPayment(Ticket ticket, PaymentMethod paymentMethod) throws PaymentException {
        if (ticket == null) {
            throw new PaymentException("Cannot process payment for null ticket");
        }
        double amount = calculateFee(ticket);
        PaymentProcessor processor = PaymentProcessorFactory.getProcessor(paymentMethod);
        return processor.process(ticket, amount);
    }

    public String getPricingStrategyName() {
        return pricingStrategy.getStrategyName();
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }
}
