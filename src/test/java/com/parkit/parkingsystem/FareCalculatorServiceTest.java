package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static com.parkit.parkingsystem.constants.Fare.DIVIDER_MINUTES_TO_HOURS;
import static com.parkit.parkingsystem.constants.Fare.REGULAR_CUSTOMER_DISCOUNT_RATE;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;


public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)), ticket.getPrice());
    }

    @Test
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.BIKE_RATE_PER_HOUR - ((Fare.BIKE_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)), ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now().plusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareCarWithOutTimeNull() {
        LocalDateTime inTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(Exception.class, () -> fareCalculatorService.calculateFare(ticket));

    }


    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR - ((Fare.BIKE_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / 60))), ticket.getPrice());
    }

    @Test
    public void calculateDurationCarForADayInParkingWithFreeTime() {
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertEquals(24 - (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS), fareCalculatorService.calculateDuration(ticket));
    }

    @Test
    public void calculateFareCarForRegularCustomer() {

        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsRegular(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))) * REGULAR_CUSTOMER_DISCOUNT_RATE), ticket.getPrice());
    }

    @Test
    public void calculateFareBikeForRegularCustomer() {

        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();

        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsRegular(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((Fare.BIKE_RATE_PER_HOUR - ((Fare.BIKE_RATE_PER_HOUR) * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))) * REGULAR_CUSTOMER_DISCOUNT_RATE), ticket.getPrice());
    }


}
