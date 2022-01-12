package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class InputReaderUtil {

    /**
     * @see Scanner
     */
    private static final Scanner SCAN
            = new Scanner(System.in, "UTF-8");

    /**
     * @see Logger
     */
    private static final Logger LOGGER
            = LogManager.getLogger("InputReaderUtil");

    /**
     * Return user choice.
     *
     * @return an integer corresponding to the choice entered by the user.
     */
    public int readSelection() {
        try {
           return Integer.parseInt(SCAN.nextLine());

        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell",
                    e);
            System.out.println("Error reading input. Please enter valid number"
                    + "for proceeding further");
            return -1;
        }

    }

    /**
     * Return user choice.
     *
     * @return a String corresponding to the choice entered by the user
     * (vehicle registration number).
     */
    public String readVehicleRegistrationNumber() {
        try {
            String vehicleRegNumber = SCAN.nextLine();
            if (vehicleRegNumber == null
                    || vehicleRegNumber.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell",
                    e);
            System.out.println("Error reading input. Please enter a valid "
                    + "string for vehicle registration number");
            throw e;
        }
    }


}
