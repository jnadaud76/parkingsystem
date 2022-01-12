package com.parkit.parkingsystem.constants;

public final class DBConstants {

    private DBConstants() {

    }

    /**
     * Get next available parking spot in database.
     */
    public static final String GET_NEXT_PARKING_SPOT =
            "select min(PARKING_NUMBER) from parking where AVAILABLE = true"
                    + " and TYPE = ?";

    /**
     * Update parking spot status.
     */
    public static final String UPDATE_PARKING_SPOT =
            "update parking set available = ? where PARKING_NUMBER = ?";

    /**
     * Save ticket in database.
     */
    public static final String SAVE_TICKET =
            "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE,"
                    + " IN_TIME, OUT_TIME, IS_REGULAR) values(?,?,?,?,?,?)";

    /**
     * Update ticket in database.
     */
    public static final String UPDATE_TICKET =
            "update ticket set PRICE=?, OUT_TIME=?, IS_REGULAR=? where ID=?";

    /**
     * Get ticket in database from provided vehicle registration number.
     */
    public static final String GET_TICKET =
            "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME,"
                    + " t.IS_REGULAR, p.TYPE from ticket t,parking p where"
                    + " p.parking_number = t.parking_number and"
                    + " t.VEHICLE_REG_NUMBER=? order by t.IN_TIME desc limit 1";

    /**
     * Counts the occurrences of a license plate in the ticket table.
     */
    public static final String COUNT_VEHICLE_REG_NUMBER_FREQUENCY =
            "SELECT COUNT(VEHICLE_REG_NUMBER) FROM ticket WHERE"
                    + " VEHICLE_REG_NUMBER=?";
}
