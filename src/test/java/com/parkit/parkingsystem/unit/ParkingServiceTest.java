package com.parkit.parkingsystem.unit;

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
import java.time.temporal.ChronoUnit;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

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
    void processExitingVehicleTest() {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //When
        parkingService.processExitingVehicle();

        //Then
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    @Test
    void givenRunTimeExceptionWhenProcessExitingVehicleTest() {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        doThrow(new RuntimeException()).when(ticketDAO).getTicket(anyString());

        //When
        parkingService.processExitingVehicle();

        //Then
        verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));

    }


    @Test
    void getNextParkingIfAvailableTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        //When
        parkingService.getNextParkingNumberIfAvailable();

        //Then
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    @Test
    void processIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    void givenParkingSpotNullWhenProcessIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    void givenRunTimeExceptionWhenProcessIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);
        doThrow(new RuntimeException()).when(ticketDAO).saveTicket(any(Ticket.class));

        //When
        parkingService.processIncomingVehicle();

        //Then
        assertNull(ticketDAO.getTicket("ABCDEF"));


    }

    @Test
    void givenInputIsOneThenGetVehicleTypeReturnCar() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);

        //When
        ParkingType result = parkingService.getVehichleType();

        //Then
        assertEquals(result.toString(), ParkingType.CAR.toString());
    }

    @Test
    void givenInputIsTwoThenGetVehicleTypeReturnBike() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);

        //When
        ParkingType result = parkingService.getVehichleType();

        //Then
        assertEquals(result.toString(), ParkingType.BIKE.toString());
    }

    @Test
    void givenInputIsThreeThenGetVehicleTypeReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(3);

        //When //Then
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    void givenInputIsMinusThreeThenGetVehicleTypeReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(-3);

        //When //Then
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    void givenParkingSpotFullThenGetNextParkingNumberIfAvailableReturnException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //When //Then
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    void givenParkingSpotWithBadParkingTypeThenGetNextParkingNumberIfAvailableReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenThrow(new IllegalArgumentException());

        //When //Then
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    void givenFailUpdateTicketThenProcessExitingVehicleReturnErrorMessage() throws UnsupportedEncodingException {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, "UTF-8");
        System.setOut(out);

        //When
        parkingService.processExitingVehicle();

        //Then
        assertTrue(outContent.toString("UTF-8").contains("Unable to update ticket information. Error occurred"));

    }
}
