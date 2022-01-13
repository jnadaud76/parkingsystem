package com.parkit.parkingsystem.constants;

public final class Fare {

    private Fare() {

    }

    /**
     * Hour fare rate for bike.
     */
    public static final double BIKE_RATE_PER_HOUR = 1.0;

    /**
     * Hour fare rate for car.
     */
    public static final double CAR_RATE_PER_HOUR = 1.5;

    /**
     * Free time in minutes.
     */
    public static final double FREE_TIME_IN_MINUTES = 30;

    /**
     * Defines the discount applied to the regular customer.
     */
    public static final double REGULAR_CUSTOMER_DISCOUNT_RATE = 0.95;

    /**
     * Define the frequency of passage necessary in order to be considered as
     * regular client.
     */
    public static final int MIN_FREQUENCY_FOR_REGULAR_CUSTOMER = 1;

    /**
     * Divider to convert minutes to hours.
     */
    public static final int DIVIDER_MINUTES_TO_HOURS = 60;
}
