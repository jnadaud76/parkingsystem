package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static com.parkit.parkingsystem.constants.Fare.DIVIDER_MINUTES_TO_HOURS;
import static com.parkit.parkingsystem.constants.Fare.FREE_TIME_IN_MINUTES;
import static com.parkit.parkingsystem.constants.Fare.REGULAR_CUSTOMER_DISCOUNT_RATE;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;


class FareCalculatorServiceTest {

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
    void calculateFareCar() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        fareCalculatorService.calculateFare(ticket);

        //Then
        assertEquals(Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))
                , ticket.getPrice());
    }

    @Test
    void calculateFareBike() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        fareCalculatorService.calculateFare(ticket);

        //Then
        assertEquals(Fare.BIKE_RATE_PER_HOUR - ((Fare.BIKE_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS))
                , ticket.getPrice());
    }

    @Test
    void calculateFareUnkownType() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When //Then
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareBikeWithFutureInTime() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().plusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When //Then
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    void calculateFareCarWithOutTimeNull() {
        //Given
        LocalDateTime inTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setParkingSpot(parkingSpot);

        //When //Then
        assertThrows(Exception.class, () -> fareCalculatorService.calculateFare(ticket));

    }

    @Test
    void calculateDurationBikeWithParkingTimeEqualFreeTime(){
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes((long)FREE_TIME_IN_MINUTES);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        double duration = fareCalculatorService.calculateDuration(ticket);

        //Then
        assertEquals(0, duration);
    }

    @Test
    void calculateFareBikeWithLessThanOneHourParkingTime() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        fareCalculatorService.calculateFare(ticket);

        //Then
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR
                - ((Fare.BIKE_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)))
                , ticket.getPrice());
    }

    @Test
    void calculateFareCarWithLessThanOneHourParkingTime() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        fareCalculatorService.calculateFare(ticket);

        //Then
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR
                - ((Fare.CAR_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)))
                , ticket.getPrice());
    }

    @Test
    void calculateFareCarWithMoreThanADayParkingTime() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When
        fareCalculatorService.calculateFare(ticket);

        //Then
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR
                - ((Fare.CAR_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / 60)))
                , ticket.getPrice());
    }

    @Test
    void calculateDurationCarForADayInParkingWithFreeTime() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusHours(24);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        //When //Then
        assertEquals(24
                - (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)
                , fareCalculatorService.calculateDuration(ticket));
    }

    @Test
    void calculateFareCarForRegularCustomer() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsRegular(true);

        //When //Then
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((Fare.CAR_RATE_PER_HOUR - ((Fare.CAR_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)))
                * REGULAR_CUSTOMER_DISCOUNT_RATE), ticket.getPrice());
    }

    @Test
    void calculateFareBikeForRegularCustomer() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setIsRegular(true);

        //When //Then
        fareCalculatorService.calculateFare(ticket);
        assertEquals(((Fare.BIKE_RATE_PER_HOUR
                - ((Fare.BIKE_RATE_PER_HOUR)
                * (Fare.FREE_TIME_IN_MINUTES / DIVIDER_MINUTES_TO_HOURS)))
                * REGULAR_CUSTOMER_DISCOUNT_RATE)
                , ticket.getPrice());
    }

}
