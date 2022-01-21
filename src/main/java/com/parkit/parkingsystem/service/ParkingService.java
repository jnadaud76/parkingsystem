package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class ParkingService {

    /**
     * @see Logger
     */
    private static final Logger LOGGER
            = LogManager.getLogger("ParkingService");

    /**
     * Service in charge of calculating the fare to
     * be paid.
     */
    private static final FareCalculatorService FARE_CALCULATOR_SERVICE
            = new FareCalculatorService();

    /**
     * Tool responsible for reading input provided by the user.
     */
    private final InputReaderUtil inputReaderUtil;

    /**
     * ParkingSpot type data access object.
     */
    private final ParkingSpotDAO parkingSpotDAO;

     /**
     * Ticket type data access object.
     */
    private final TicketDAO ticketDAO;

     /**
     * ParkingService constructor.
     *
     * @param inputReaderUtilParam
     * Tool responsible for reading input provided by the user.
     * @param parkingSpotDAOParam
     * ParkingSpot type data access object.
     * @param ticketDAOParam
     * Ticket type data access object.
     */
    public ParkingService(final InputReaderUtil inputReaderUtilParam,
                          final ParkingSpotDAO parkingSpotDAOParam,
                          final TicketDAO ticketDAOParam) {
        this.inputReaderUtil = inputReaderUtilParam;
        this.parkingSpotDAO = parkingSpotDAOParam;
        this.ticketDAO = ticketDAOParam;

    }

    /**
     * Process for vehicles entering parking lot.
     */
    public void processIncomingVehicle() {

        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();
                Ticket ticket2 = ticketDAO.getTicket(vehicleRegNumber);
                if (ticket2 != null && ticket2.getOutTime() == null) {
                    System.out.println("The registered vehicle "
                            + vehicleRegNumber + " is already parked in the "
                            + "parking lot");
                   throw new Exception();
                }
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);
                boolean isRegular = ticketDAO.isRegularCustomer(ticket2);
                if (isRegular) {
                    System.out.println("Welcome back! As a recurring user of "
                            + "our parking lot, you'll benefit from a 5% "
                            + "discount");
                }

                LocalDateTime inTime = LocalDateTime.now()
                        .truncatedTo(ChronoUnit.MINUTES);
                Ticket ticket = new Ticket();
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticket.setIsRegular(isRegular);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"
                        + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"
                        + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * Give vehicle registration number.
     *
     * @return vehicle registration number as String.
     */
    private String getVehichleRegNumber() {
        System.out.println("Please type the vehicle registration number and"
                + " press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();

    }


    /**
     * Give next Parking Spot if available.
     *
     * @return a free ParkingSpot.
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber,
                        parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. "
                        + "Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            LOGGER.error("Error parsing user input for type of vehicle",
                    ie);
        } catch (Exception e) {
            LOGGER.error("Error fetching next available parking slot",
                    e);
        }
        return parkingSpot;
    }

    /**
     * Give vehicle type.
     *
     * @return a ParkingType.
     */
    public ParkingType getVehichleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;

            case 2:
                return ParkingType.BIKE;

            default:
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");

        }
    }

    /**
     * Process for vehicles exiting parking lot.
     */
    public void processExitingVehicle() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
           if (ticket != null && ticket.getOutTime() != null) {
                System.out.println("The registered vehicle "
                        + vehicleRegNumber + " isn't parked in the "
                        + "parking lot");
                throw new Exception();
            }
            LocalDateTime outTime = LocalDateTime.now()
                              .truncatedTo(ChronoUnit.MINUTES);
           if (ticket != null) {
               ticket.setOutTime(outTime);
               FARE_CALCULATOR_SERVICE.calculateFare(ticket);
           }
           if (ticket != null && ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:"
                        + df.format(ticket.getPrice()));
                System.out.println("Recorded out-time for vehicle number:"
                        + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                System.out.println("Unable to update ticket information. Error "
                        + "occurred");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process exiting vehicle", e);
        }
    }
}
