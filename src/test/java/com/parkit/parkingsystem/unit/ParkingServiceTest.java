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
    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        ticket = new Ticket();
    }

    @Test
    void processExitingVehicleTest() {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //When
        parkingService.processExitingVehicle();

        //Then
        assertNotNull(ticket.getOutTime());
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

    }

    @Test
    void givenRunTimeException_WhenProcessExitingVehicleTest() {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        doThrow(new RuntimeException()).when(ticketDAO).getTicket(anyString());

        //When
        parkingService.processExitingVehicle();

        //Then
        assertNull(ticket.getOutTime());
        verify(ticketDAO, Mockito.times(0)).updateTicket(any(Ticket.class));

    }


    @Test
    void getNextParkingIfAvailableTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(4);

        //When
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        //Then
        assertEquals(4,parkingSpot.getId());
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    @Test
    void processIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
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
    void givenParkingSpotNull_WhenProcessIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    @Test
    void givenRunTimeException_WhenProcessIncomingVehicleTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
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
    void givenInputIsOne_WhenGetVehicleType_ThenReturnCar() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);

        //When
        ParkingType result = parkingService.getVehichleType();

        //Then
        assertEquals(result.toString(), ParkingType.CAR.toString());
    }

    @Test
    void givenInputIsTwo_WhenGetVehicleType_ThenReturnBike() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);

        //When
        ParkingType result = parkingService.getVehichleType();

        //Then
        assertEquals(result.toString(), ParkingType.BIKE.toString());
    }

    @Test
    void givenInputIsThree_WhenGetVehicleType_ThenReturnIllegalArgumentException() {
        //Given
       when(inputReaderUtil.readSelection()).thenReturn(3);

        //When //Then
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    void givenInputIsMinusThree_WhenGetVehicleType_ThenReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(-3);

        //When //Then
        assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
    }

    @Test
    void givenParkingSpotFull_WhenGetNextParkingNumberIfAvailable_ThenReturnException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);

        //When //Then
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    void givenParkingSpotWithBadParkingType_WhenGetNextParkingNumberIfAvailable_ThenReturnIllegalArgumentException() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenThrow(new IllegalArgumentException());

        //When //Then
        assertNull(parkingService.getNextParkingNumberIfAvailable());

    }

    @Test
    void givenFailUpdateTicket_WhenProcessExitingVehicle_ThenReturnErrorMessage() throws UnsupportedEncodingException {
        //Given
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
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
