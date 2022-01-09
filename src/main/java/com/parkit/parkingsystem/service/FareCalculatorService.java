package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.parkit.parkingsystem.constants.Fare.DIVIDER_MINUTES_TO_HOURS;


public class FareCalculatorService {

     /**
     * Method that calculates the price to pay and applies a 5 percent discount
     * for regular customers.
     *
     * @param ticket
     */
    public void calculateFare(final Ticket ticket) {

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                if (ticket.getIsRegular()) {
                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.CAR_RATE_PER_HOUR
                            * Fare.REGULAR_CUSTOMER_DISCOUNT_RATE);
                } else {
                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.CAR_RATE_PER_HOUR);
                }
                break;
            case BIKE:
                if (ticket.getIsRegular()) {
                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.BIKE_RATE_PER_HOUR
                            * Fare.REGULAR_CUSTOMER_DISCOUNT_RATE);
                } else {
                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.BIKE_RATE_PER_HOUR);
                }
                break;
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    /**
     * Method to check the time in the parking lot and apply if under 30 minutes
     * then free.
     * @param ticket
     * @return duration in minutes
     */
    public double calculateDuration(final Ticket ticket) {
        if ((ticket.getOutTime() == null)
                || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is "
                    + "incorrect:" + ticket.getOutTime().toString());
        }
        double duration;
        LocalDateTime inDate = ticket.getInTime();
        LocalDateTime outDate = ticket.getOutTime();

        long durationInMinutes = Duration.between(inDate, outDate).toMinutes();

        if (durationInMinutes <= Fare.FREE_TIME_IN_MINUTES) {
            duration = 0;
        } else {
            duration =
                    ((double) durationInMinutes - Fare.FREE_TIME_IN_MINUTES)
                            / DIVIDER_MINUTES_TO_HOURS;
        }
        return duration;
    }


}
