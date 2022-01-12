package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.parkit.parkingsystem.constants.Fare.DIVIDER_MINUTES_TO_HOURS;
import static com.parkit.parkingsystem.constants.Fare.REGULAR_CUSTOMER_DISCOUNT_RATE;


public class FareCalculatorService {

     /**
     * Method that calculates the price to pay and applies a 5 percent discount
     * for regular customers.
     *
     * @param ticket ticket.
     */
    public void calculateFare(final Ticket ticket) {
        double fareRate = calculateFareRate(ticket);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:

                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.CAR_RATE_PER_HOUR
                            * fareRate);
                break;
            case BIKE:

                    ticket.setPrice(calculateDuration(ticket)
                            * Fare.BIKE_RATE_PER_HOUR
                            * fareRate);

                break;
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    /**
     * Method to check the time in the parking lot and apply if under 30 minutes
     * then free.
     * @param ticket ticket.
     * @return duration in hours
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
                    (durationInMinutes - Fare.FREE_TIME_IN_MINUTES)
                            / DIVIDER_MINUTES_TO_HOURS;
        }
        return duration;
    }

    /**
     * Method to know if recurring user, if so then we return
     * 0.95, if not we return 1.
     *
     * @param ticket ticket
     * @return fareRate.
     */
    public double calculateFareRate(final Ticket ticket) {
        double fareRate;
        if (ticket.getIsRegular()) {
            fareRate = REGULAR_CUSTOMER_DISCOUNT_RATE;
        } else {
            fareRate = 1;
        }
        return fareRate;
    }
}
