package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Ticket {
    /**
     * Ticket id.
     */
    private int id;
    /**
     * Ticket parking spot.
     */
    private ParkingSpot parkingSpot;
    /**
     * Ticket vehicle registration number.
     */
    private String vehicleRegNumber;
    /**
     * Ticket price.
     */
    private double price;
    /**
     * Ticket in time.
     */
    private LocalDateTime inTime;
    /**
     * Ticket out time.
     */
    private LocalDateTime outTime;
    /**
     * Determine if a customer is regular.
     */
    private boolean isRegular;

    /**
     * Getter.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter.
     *
     * @param idParam
     */
    public void setId(final int idParam) {
        this.id = idParam;
    }

    /**
     * Getter.
     *
     * @return parkingSpot
     */
    public ParkingSpot getParkingSpot() {
        return new ParkingSpot(parkingSpot.getId(),
                parkingSpot.getParkingType(),
                parkingSpot.isAvailable());
    }

    /**
     * Setter.
     *
     * @param parkingSpotParam
     */
    public void setParkingSpot(final ParkingSpot parkingSpotParam) {
        this.parkingSpot = parkingSpotParam == null
                ? null
                : new ParkingSpot(parkingSpotParam.getId(),
                parkingSpotParam.getParkingType(),
                parkingSpotParam.isAvailable());
    }

    /**
     * Getter.
     *
     * @return vehicleRegNumber
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * Setter.
     *
     * @param vehicleRegNumberParam
     */
    public void setVehicleRegNumber(final String vehicleRegNumberParam) {
        this.vehicleRegNumber = vehicleRegNumberParam;
    }

    /**
     * Getter.
     *
     * @return price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter.
     *
     * @param priceParam
     */
    public void setPrice(final double priceParam) {
        this.price = priceParam;
    }

    /**
     * Getter.
     *
     * @return inTime
     */
    public LocalDateTime getInTime() {
        return inTime;
    }

    /**
     * Setter.
     * DateTimeFormatter is used to avoid time conversion problems with
     * Timestamp in TicketDAO (nanosecond suppression).
     *
     * @param inTimeParam
     */
    public void setInTime(final LocalDateTime inTimeParam) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = inTimeParam.format(formatter);
        this.inTime = LocalDateTime.parse(formatDateTime, formatter);
    }

    /**
     * Getter.
     *
     * @return outTime
     */
    public LocalDateTime getOutTime() {
        return outTime;
    }

    /**
     * Setter.
     *
     * @param outTimeParam
     */
    public void setOutTime(final LocalDateTime outTimeParam) {
        this.outTime = outTimeParam;
    }

    /**
     * Getter.
     *
     * @return isRegular
     */
    public boolean getIsRegular() {
        return isRegular;
    }

    /**
     * Setter.
     *
     * @param regular
     */
    public void setIsRegular(final boolean regular) {
        isRegular = regular;
    }
}
