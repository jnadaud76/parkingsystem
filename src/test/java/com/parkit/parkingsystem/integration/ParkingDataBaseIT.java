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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void testParkingACar() {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //When
        parkingService.processIncomingVehicle();

        //Then
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        assertEquals("ABCDEF", ticket.getVehicleRegNumber());
        assertEquals(new ParkingSpot(1, ParkingType.CAR, false), ticket.getParkingSpot());
        assertNotNull(ticket.getInTime());
        assertNull(ticket.getOutTime());
        assertEquals(0, ticket.getPrice());
        assertFalse(ticket.getIsRegular());
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    void testParkingLotExit() {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        parkingService.processIncomingVehicle();

        //when
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //Then
        assertEquals((fareCalculatorService.calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        assertEquals(1,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        assertNotNull(ticket.getOutTime());
    }

    @Test
    void testParkingACarWhichIsAlreadyInsideParkingLot() throws UnsupportedEncodingException {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, "UTF-8");
        System.setOut(out);

        //when
        parkingService.processIncomingVehicle();

        //Then
        assertTrue(outContent.toString("UTF-8").contains("The registered vehicle ABCDEF is already parked in the parking lot"));
    }

    @Test
    void testParkingACarWhichReturningToParkingOneYearLater() {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
           ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setPrice(30);
            ticket.setInTime(LocalDateTime.now().minusYears(1).truncatedTo(ChronoUnit.MINUTES));
            ticket.setOutTime(LocalDateTime.now().minusYears(1).plusHours(1).truncatedTo(ChronoUnit.MINUTES));
            ticket.setIsRegular(false);
            ticketDAO.saveTicket(ticket);
            parkingService.processIncomingVehicle();

        //When
            parkingService.processExitingVehicle();
            Ticket ticket2 = ticketDAO.getTicket("ABCDEF");
            double price = ticket2.getPrice();

         //Then
         assertEquals(0, price);
         assertTrue(ticket2.getIsRegular());
    }

    @Test
    void testExitingACarWhichAlreadyExitingParkingLot() throws UnsupportedEncodingException {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, "UTF-8");
        System.setOut(out);

        //When
        parkingService.processExitingVehicle();

        //Then
        assertTrue(outContent.toString("UTF-8").contains("The registered vehicle ABCDEF isn't parked in the parking lot"));
    }
}
