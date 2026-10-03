package com.parkinglot.strategy.pricing;

import com.parkinglot.models.Ticket;

/**
 * Applies a surge multiplier (1.5x) over the standard hourly rate.
 */
public class PeakHourPricingStrategy implements PricingStrategy {

    private final HourlyPricingStrategy baseStrategy;
    private final double surgeMultiplier;

    public PeakHourPricingStrategy(double surgeMultiplier) {
        this.baseStrategy = new HourlyPricingStrategy();
        this.surgeMultiplier = surgeMultiplier;
    }

    public PeakHourPricingStrategy() {
        this(1.5);
    }

    @Override
    public double calculateFee(Ticket ticket) {
        double baseFee = baseStrategy.calculateFee(ticket);
        return baseFee * surgeMultiplier;
    }

    @Override
    public String getStrategyName() {
        return "Peak Hour Pricing Strategy (" + surgeMultiplier + "x)";
    }
}
