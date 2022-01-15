package com.parkit.parkingsystem.integration;

import static com.parkit.parkingsystem.constants.Fare.MIN_FREQUENCY_FOR_REGULAR_CUSTOMER;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;


class TicketDAOIT {
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() {
        ticketDAO = new TicketDAO(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() {

        dataBasePrepareService.clearDataBaseEntries();

    }

    @Test
    void givenTicketWithParkingSpotNull_WhenSaveTicket_ThenReturnFalse() {
        //Given
        LocalDateTime inTime = LocalDateTime.now();
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(null);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setIsRegular(false);

        //When//Then
        assertFalse(ticketDAO.saveTicket(ticket));

    }

    @Test
    void givenTicket_WhenSaveTicket_ThenReturnTrue() {
        //Given
        LocalDateTime inTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setIsRegular(false);

        //When//Then
        assertTrue(ticketDAO.saveTicket(ticket));

    }

    @Test
    void givenEmptyDataBase_WhenGetTicket_ThenReturnNull() {
        //Given

        //When
        Ticket ticket = ticketDAO.getTicket("EFGHIJ");

        //Then
        assertNull(ticket);
    }

    @Test
    void givenDataBaseWithOneTicket_WhenGetTicket_ThenReturnTicket() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setIsRegular(false);
        ticketDAO.saveTicket(ticket);

        //When
        Ticket ticket2 = ticketDAO.getTicket("ABCDEF");

        //Then
        assertEquals(ticket.getInTime(), ticket2.getInTime());
        assertNotNull(ticket2);
    }

    @Test
    void givenTicket_WhenIsRegularCustomer_ThenReturnFalse() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusHours(1).truncatedTo(ChronoUnit.SECONDS);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setIsRegular(false);

        //When //Then
        assertFalse(ticketDAO.isRegularCustomer(ticket));
    }

    @Test
    void givenDataBaseWithOneTicketAndTicket_WhenIsRegularCustomer_ThenReturnTrue() {
        //Given
        LocalDateTime inTime = LocalDateTime.now()
                .minusHours(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime outTime = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        for(int i=0; i<MIN_FREQUENCY_FOR_REGULAR_CUSTOMER; i++) {
            Ticket ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setPrice(0);
            ticket.setInTime(inTime);
            ticket.setOutTime(outTime);
            ticket.setIsRegular(false);
            ticketDAO.saveTicket(ticket);
        }

        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);

        //When //Then
        assertTrue(ticketDAO.isRegularCustomer(ticket));
    }

    @Test
    void givenTicketWithNullOutTime_WhenUpdateTicket_ThenReturnFalse() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        ticket.setIsRegular(false);

        //When //Then
        assertFalse(ticketDAO.updateTicket(ticket));
    }

    @Test
    void givenTicket_WhenUpdateTicket_ThenReturnTrue() {
        //Given
        LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setIsRegular(false);

        //When//Then
        assertTrue(ticketDAO.updateTicket(ticket));
    }

    @Test
    void givenNullTicket_WhenTryToKnowIfCustomerIsRegular_ThenReturnFalse() {
        //Given
        Ticket ticket = null;

        //When//Then
        assertFalse(ticketDAO.isRegularCustomer(ticket));
    }
}