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
    void testParkingACar() {
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
        assertNull(ticket.getOutTime());
        assertEquals(0, ticket.getPrice());
        assertFalse(ticket.getIsRegular());
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    void testParkingLotExit() throws Exception {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        parkingService.processIncomingVehicle();

        //when
        parkingService.processExitingVehicle();
        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        //TODO: check that the fare generated and out time are populated correctly in the database
        //Then
        assertEquals((fareCalculatorService.calculateDuration(ticket) * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
        assertEquals(1,parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        assertNotNull(ticket.getOutTime());
    }

    @Test
    void testParkingACarForAlreadyInsideParkingLot() throws UnsupportedEncodingException {
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //for (int i = 0; i < MIN_FREQUENCY_FOR_REGULAR_CUSTOMER; i++) {
           /* ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            ticket.setPrice(0);
            ticket.setInTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            ticket.setOutTime(null);
            ticket.setIsRegular(false);
            ticketDAO.saveTicket(ticket);*/
        parkingService.processIncomingVehicle();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, "UTF-8");
        System.setOut(out);


        //when
        parkingService.processIncomingVehicle();

        //Then
        assertTrue(outContent.toString("UTF-8").contains("This vehicle is already parked in the parking lot"));
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
            ticket.setInTime(LocalDateTime.now().minusYears(1).truncatedTo(ChronoUnit.SECONDS));
            ticket.setOutTime(LocalDateTime.now().minusYears(1).plusHours(1).truncatedTo(ChronoUnit.SECONDS));
            ticket.setIsRegular(false);
            ticketDAO.saveTicket(ticket);
            parkingService.processIncomingVehicle();

        //When
            parkingService.processExitingVehicle();
            double price = ticketDAO.getTicket("ABCDEF").getPrice();

         //Then
         assertEquals(0, price);
    }


}
