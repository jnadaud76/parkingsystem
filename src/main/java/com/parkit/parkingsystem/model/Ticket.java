package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private boolean isRegular;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ParkingSpot getParkingSpot() {
        return new ParkingSpot(parkingSpot.getId(),parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot==null ? null : new ParkingSpot(parkingSpot.getId(),parkingSpot.getParkingType(), parkingSpot.isAvailable());
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = inTime.format(formatter);
        this.inTime = LocalDateTime.parse(formatDateTime, formatter);
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    public boolean getIsRegular() {
        return isRegular;
    }

    public void setIsRegular(boolean regular) {
        isRegular = regular;
    }
}
