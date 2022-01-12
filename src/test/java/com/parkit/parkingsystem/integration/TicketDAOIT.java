package com.parkit.parkingsystem.integration;

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
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {

        dataBasePrepareService.clearDataBaseEntries();

    }

    @Test
    void givenTicketWithParkingSpotNullWhenSaveTicketThenReturnFalse() {
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
    void givenTicketWhenSaveTicketThenReturnTrue() {
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
    void givenEmptyDataBaseWhenGetTicketThenReturnNull() {
        //Given

        //When
        Ticket ticket = ticketDAO.getTicket("EFGHIJ");

        //Then
        assertNull(ticket);
    }

    @Test
    void givenDataBaseWithOneTicketWhenGetTicketThenReturnTicket() {
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
    void givenTicketWithNullOutTimeWhenUpdateTicketThenReturnFalse() {
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

        //When//Then
        assertFalse(ticketDAO.updateTicket(ticket));
    }

    @Test
    void givenTicketWhenUpdateTicketThenReturnTrue() {
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
    void givenNullTicketWhenTryToKnowIfCustomerIsRegularThenReturnFalse() {
        //Given
        Ticket ticket = null;

        //When//Then
        assertFalse(ticketDAO.isRegularCustomer(ticket));
    }
}