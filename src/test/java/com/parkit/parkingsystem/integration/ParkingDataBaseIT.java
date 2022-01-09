package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static com.parkit.parkingsystem.constants.Fare.MIN_FREQUENCY_FOR_REGULAR_CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

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
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();

    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void testParkingACar() {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //When
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        //Then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(new ParkingSpot(1, ParkingType.CAR, false), ticket.getParkingSpot());
        assertNotNull(ticket.getInTime());
        assertTrue(ticket.getOutTime() == null);
        assertEquals(0, ticket.getPrice());
        assertFalse(ticket.getIsRegular());
        assertTrue(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR) == 2);
    }

    @Test
    public void testParkingLotExit() throws Exception {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        parkingService.processIncomingVehicle();

        //when
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        //Then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertEquals((fareCalculatorService.calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        assertTrue(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR) == 1);
        assertFalse(ticket.getOutTime() == null);
    }

    @Test
    public void testParkingLotExitForRegularCustomer() {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        for (int i = 0; i < MIN_FREQUENCY_FOR_REGULAR_CUSTOMER; i++) {
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setPrice(0);
            ticket.setInTime(LocalDateTime.now());
            ticket.setOutTime(null);
            ticketDAO.saveTicket(ticket);

        }
        parkingService.processIncomingVehicle();
        //when
        parkingService.processExitingVehicle();
        //Then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertTrue(ticket.getIsRegular());
    }


}
