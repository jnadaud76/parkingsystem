package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.time.LocalDateTime;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {


    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    public void processExitingVehicleTest() {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    @Test
    public void givenRunTimeExceptionWhenProcessExitingVehicleTest() {

        when(inputReaderUtil.readVehicleRegistrationNumber())
                .thenReturn("ABCDEF");


        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        doThrow(new RuntimeException()).when(ticketDAO).getTicket(anyString());

        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));

    }


    @Test
    public void getNextParkingIfAvailableTest() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.getNextParkingNumberIfAvailable();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    @Test
    public void processIncomingVehicleTest() {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, IS_REGULAR)
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    public void givenParkingSpotNullWhenProcessIncomingVehicleTest() {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        parkingService.processIncomingVehicle();

        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    public void givenRunTimeExceptionWhenProcessIncomingVehicleTest() {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            e.printStackTrace();
        }
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, IS_REGULAR)
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);
        doThrow(new RuntimeException()).when(ticketDAO).saveTicket(any(Ticket.class));

        parkingService.processIncomingVehicle();

        assertNull(ticketDAO.getTicket("ABCDEF"));


    }

    @Test
    public void givenInputIsOneThenGetVehicleTypeReturnCar() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingType result = parkingService.getVehichleType();
        assertEquals(result.toString(), ParkingType.CAR.toString());
    }

    @Test
    public void givenInputIsTwoThenGetVehicleTypeReturnBike() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingType result = parkingService.getVehichleType();
        assertEquals(result.toString(), ParkingType.BIKE.toString());
    }

    @Test
    public void givenInputIsThreeThenGetVehicleTypeReturnIllegalArgumentException() {
        when(inputReaderUtil.readSelection()).thenReturn(3);
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    public void givenInputIsMinusThreeThenGetVehicleTypeReturnIllegalArgumentException() {
        when(inputReaderUtil.readSelection()).thenReturn(-3);
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    public void givenParkingSpotFullThenGetNextParkingNumberIfAvailableReturnException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    public void givenParkingSpotWithBadParkingTypeThenGetNextParkingNumberIfAvailableReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenThrow(new IllegalArgumentException());
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    public void givenFailUpdateTicketThenProcessExitingVehicleReturnErrorMessage() throws UnsupportedEncodingException {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");


        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        //When
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, "UTF-8");
        System.setOut(out);
        parkingService.processExitingVehicle();

        //Then
        assertTrue(outContent.toString("UTF-8").contains("Unable to update ticket information. Error occurred"));

    }
}
