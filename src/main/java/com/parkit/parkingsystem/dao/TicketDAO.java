package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.dao.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

import static com.parkit.parkingsystem.constants.Fare.MIN_FREQUENCY_FOR_REGULAR_CUSTOMER;

public class TicketDAO {

    /**
     * @see Logger
     */
    private static final Logger LOGGER
            = LogManager.getLogger("TicketDAO");

    /**
     * Instance of DataBaseConfig.
     */
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Getter.
     * @return dataBaseConfig.
     */
    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    /**
     * Setter.
     * @param dataBaseConfigParam dataBaseConfig.
     */
    public void setDataBaseConfig(final DataBaseConfig dataBaseConfigParam) {
        this.dataBaseConfig = dataBaseConfigParam;
    }

    /**
     * Default constructor.
     */
    public TicketDAO() {

    }

    /**
     * Copy constructor.
     *
     * @param ticketDAO ticketDAO
     */
    public TicketDAO(final TicketDAO ticketDAO) {
       this.dataBaseConfig = ticketDAO.dataBaseConfig;
    }

    /**
     * Save a ticket provided in a database.
     *
     * @param ticket ticket.
     * @return boolean.
     */
    public boolean saveTicket(final Ticket ticket) {
        Connection con = null;
        boolean bol = false;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME,
            // IS_REGULAR)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4,
                    Timestamp.valueOf(ticket.getInTime()
                            .truncatedTo(ChronoUnit.SECONDS)));
            ps.setTimestamp(5, (ticket.getOutTime() == null)
                    ? null : (Timestamp.valueOf(ticket.getOutTime()
                    .truncatedTo(ChronoUnit.SECONDS))));
            ps.setBoolean(6, (ticket.getIsRegular()));
            ps.execute();
            bol = true;

        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);

        }
        return bol;
    }

    /**
     * Get a ticket in database from a vehicle registration number provided.
     *
     * @param vehicleRegNumber vehicle registration number.
     * @return Ticket.
     */
    public Ticket getTicket(final String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET);
            ps.setString(1, vehicleRegNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot
                        = new ParkingSpot(rs.getInt(1),
                        ParkingType.valueOf(rs.getString(7)),
                        false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4)
                        .toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
                ticket.setOutTime(rs.getTimestamp(5) == null ? null
                        : rs.getTimestamp(5).toLocalDateTime()
                        .truncatedTo(ChronoUnit.SECONDS));
                ticket.setIsRegular(rs.getBoolean(6));
            }

        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    /**
     * Update ticket provided in database.
     *
     * @param ticket ticket.
     * @return boolean.
     */
    public boolean updateTicket(final Ticket ticket) {
        Connection con = null;
        boolean bol = false;
        PreparedStatement ps = null;

        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2,
                    Timestamp.valueOf(ticket.getOutTime()
                            .truncatedTo(ChronoUnit.SECONDS)));
            ps.setBoolean(3, ticket.getIsRegular());
            ps.setInt(4, ticket.getId());
            ps.execute();
            bol = true;

        } catch (Exception ex) {
            LOGGER.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return bol;
    }

    /**
     * Determine if a customer is regular from the analysis of occurrences
     * of his vehicle registration number in the database.
     *
     * @param ticket ticket.
     * @return boolean.
     */
    public boolean isRegularCustomer(final Ticket ticket) {
        Connection con = null;
        int result = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean bol = false;

        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants
                    .COUNT_VEHICLE_REG_NUMBER_FREQUENCY);
            ps.setString(1, ticket.getVehicleRegNumber());
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);

            }
            if (result >= MIN_FREQUENCY_FOR_REGULAR_CUSTOMER) {
                bol = true;
            }

        } catch (Exception ex) {
            LOGGER.error("Error counting frequency of vehicule "
                    + "registration number", ex);
        } finally {
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }
        return bol;
    }
}
