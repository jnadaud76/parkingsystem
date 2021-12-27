package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        /* int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();*/

        LocalDateTime inDate = ticket.getInTime();
        LocalDateTime outDate = ticket.getOutTime();

        long duration = Duration.between(inDate, outDate).toMinutes();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        /*double duration = outHour - inHour;*/
        //double duration = (outDate.getTime() - inDate.getTime())/(1000*60*60);

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    //Méthode pour vérifier le temps dans le parking et appliquer si <30 minutes alors free

    // Méthode pour la réduction 5%
}