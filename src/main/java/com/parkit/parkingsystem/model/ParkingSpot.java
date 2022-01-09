package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {

    /**
     * Number of parking spot.
     */
    private int number;

    /**
     * Parking type of parking spot.
     */
    private ParkingType parkingType;

    /**
     * Availability of parking spot.
     */
    private boolean isAvailable;

    /**
     * ParkingSpot constructor.
     *
     * @param numberParam
     * Number of parking spot.
     * @param parkingTypeParam
     * Parking type of parking spot.
     * @param isAvailableParam
     * Availability of parking spot.
     */
    public ParkingSpot(final int numberParam,
                       final ParkingType parkingTypeParam,
                       final boolean isAvailableParam) {
        this.number = numberParam;
        this.parkingType = parkingTypeParam;
        this.isAvailable = isAvailableParam;
    }

    /**
     * Getter.
     * @return number
     */
    public int getId() {
        return number;
    }

    /**
     * Setter.
     * @param numberParam
     */
    public void setId(final int numberParam) {
        this.number = numberParam;
    }

    /**
     * Getter.
     * @return parkingType
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * Setter.
     * @param parkingTypeParam
     */
    public void setParkingType(final ParkingType parkingTypeParam) {
        this.parkingType = parkingTypeParam;
    }

    /**
     * Getter.
     * @return isAvailable
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Setter.
     * @param available
     */
    public void setAvailable(final boolean available) {
        isAvailable = available;
    }

    /**
     * equals method.
     * @param o
     * @return boolean
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    /**
     * hashCode method.
     * @return number
     */
    @Override
    public int hashCode() {
        return number;
    }
}
