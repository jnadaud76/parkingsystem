package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class ParkingSpotDAOIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig=dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig=dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {

        dataBasePrepareService.clearDataBaseEntries();

    }

    @Test
    void givenDataBaseWithNoEmptySlotWhenGetNextAvailableSlotForCarThenReturnMinusOne() {
        //Given
        for (int i = 0; i < 3; i++) {
            ParkingSpot parkingSpot = new ParkingSpot(i + 1, ParkingType.CAR, false);
            parkingSpotDAO.updateParking(parkingSpot);
        }
        //When
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        //Then
        assertEquals(0, result);

    }

    @Test
    void givenDataBaseWithNoEmptySlotForCarButOneEmptySlotForBikeWhenGetNextAvailableSlotForBikeThenReturnOne() {
        //Given
        for (int i = 0; i < 3; i++) {
            ParkingSpot parkingSpot = new ParkingSpot(i + 1, ParkingType.CAR, false);
            parkingSpotDAO.updateParking(parkingSpot);

        }
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);
        parkingSpotDAO.updateParking(parkingSpot);

        //When
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

        //Then
        assertEquals(5, result);

    }

    @Test
    void givenParkingSpotWithNoExistenceInDataBaseWhenUpdateParkingThenReturnFalse() {
        //Given
        ParkingSpot parkingSpot = new ParkingSpot(10, ParkingType.CAR, false);

        //When //Then
        assertFalse(parkingSpotDAO.updateParking(parkingSpot));

    }

    @Test
    void givenParkingSpotNumberFourWhenUpdateParkingThenReturnTrue() {
        //Given
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        //When //Then
        assertTrue(parkingSpotDAO.updateParking(parkingSpot));
    }
}
