package com.parkit.parkingsystem.service;


import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;




public class FareCalculatorService {

    //Methode qui calcule le prix à payer et applique une remise de 5 pour cent pour les clients réguliers

    public void calculateFare(Ticket ticket){

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                if (ticket.getIsRegular()) {
                    ticket.setPrice(calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR * Fare.REGULAR_CUSTOMER_DISCOUNT_RATE);
                }else{
                    ticket.setPrice(calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR);
                }
                break;
            }
            case BIKE: {
                if (ticket.getIsRegular()) {
                    ticket.setPrice(calculateDuration(ticket) * Fare.BIKE_RATE_PER_HOUR * Fare.REGULAR_CUSTOMER_DISCOUNT_RATE);
                }else {
                    ticket.setPrice(calculateDuration(ticket) * Fare.BIKE_RATE_PER_HOUR);
                }
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
        double duration;
        LocalDateTime inDate = ticket.getInTime();
        LocalDateTime outDate = ticket.getOutTime();

        long durationInMinutes = Duration.between(inDate, outDate).toMinutes();

        if (durationInMinutes<=Fare.FREE_TIME_IN_MINUTES){
            duration = 0;
        } else {
            duration = ((double) durationInMinutes-Fare.FREE_TIME_IN_MINUTES) / 60;
        }
        return duration;
}


}
