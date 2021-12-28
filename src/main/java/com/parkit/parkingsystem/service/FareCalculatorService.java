package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        /*if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inDate = ticket.getInTime();
        LocalDateTime outDate = ticket.getOutTime();

        long durationInMinutes = Duration.between(inDate, outDate).toMinutes();
        double duration = (double)durationInMinutes/60;*/

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(calculateDuration(ticket) * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    //Méthode pour vérifier le temps dans le parking et appliquer si <30 minutes alors free

    public double calculateDuration (Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        Double duration;
        LocalDateTime inDate = ticket.getInTime();
        LocalDateTime outDate = ticket.getOutTime();

        long durationInMinutes = Duration.between(inDate, outDate).toMinutes();

        if (durationInMinutes<=Fare.FREE_TIME_IN_MINUTES){
            duration = (double) 0;
        } else {
            duration = ((double) durationInMinutes-30) / 60;
        }
        return duration;
}
    // Méthode pour la réduction 5%
}