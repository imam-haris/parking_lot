package com.parkinglot.strategy.pricing;

import com.parkinglot.models.Ticket;

/**
 * Strategy interface for calculating parking fees.
 */
public interface PricingStrategy {

    double calculateFee(Ticket ticket);

    String getStrategyName();
}
