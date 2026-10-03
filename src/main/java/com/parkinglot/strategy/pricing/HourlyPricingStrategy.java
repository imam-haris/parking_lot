package com.parkinglot.strategy.pricing;

import com.parkinglot.enums.VehicleType;
import com.parkinglot.models.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Standard hourly rate calculation.
 * Rates:
 *   - Bike  : ₹10 / hour
 *   - Car   : ₹20 / hour
 *   - Truck : ₹50 / hour
 * Minimum charge is 1 hour.
 */
public class HourlyPricingStrategy implements PricingStrategy {

    private static final double BIKE_HOURLY_RATE = 10.0;
    private static final double CAR_HOURLY_RATE = 20.0;
    private static final double TRUCK_HOURLY_RATE = 50.0;

    @Override
    public double calculateFee(Ticket ticket) {
        if (ticket == null || ticket.getVehicle() == null) {
            return 0.0;
        }

        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();

        long minutes = Math.max(1, Duration.between(entryTime, exitTime).toMinutes());
        long hours = (long) Math.ceil((double) minutes / 60.0);
        if (hours < 1) hours = 1;

        double ratePerHour = getRatePerHour(ticket.getVehicle().getType());
        return hours * ratePerHour;
    }

    public double getRatePerHour(VehicleType type) {
        if (type == null) return CAR_HOURLY_RATE;
        switch (type) {
            case BIKE:
                return BIKE_HOURLY_RATE;
            case CAR:
                return CAR_HOURLY_RATE;
            case TRUCK:
                return TRUCK_HOURLY_RATE;
            default:
                return CAR_HOURLY_RATE;
        }
    }

    @Override
    public String getStrategyName() {
        return "Hourly Pricing Strategy";
    }
}
