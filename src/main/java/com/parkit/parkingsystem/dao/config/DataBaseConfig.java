package com.parkit.parkingsystem.dao.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ResourceBundle;

public class DataBaseConfig {
    /**
     * @see Logger
     */
    private static final Logger LOGGER
            = LogManager.getLogger("DataBaseConfig");

    /**
     * Open connection to prod's database.
     *
     * @return a Connection to the prod's database.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException,
            SQLException {

        ResourceBundle res = ResourceBundle.getBundle("jdbc");
        String url = res.getString("url");
        String username = res.getString("username");
        String password = res.getString("password");
        LOGGER.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Close connection to prod's database.
     *
     * @see Connection
     * @param con a Connection previously open to prod's database.
     */
    public void closeConnection(final Connection con) {
        if (con != null) {
            try {
                con.close();
                LOGGER.info("Closing DB connection");
            } catch (SQLException e) {
                LOGGER.error("Error while closing connection", e);
            }
        }
    }

    /**
     * Close PreparedStatement.
     *
     * @see PreparedStatement
     * @param ps a PreparedStatement previously open.
     */
    public void closePreparedStatement(final PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
                LOGGER.info("Closing Prepared Statement");
            } catch (SQLException e) {
                LOGGER.error("Error while closing prepared statement",
                        e);
            }
        }
    }

    /**
     * Close ResultSet.
     *
     * @see ResultSet
     * @param rs a ResultSet previously open.
     */
    public void closeResultSet(final ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                LOGGER.info("Closing Result Set");
            } catch (SQLException e) {
                LOGGER.error("Error while closing result set", e);
            }
        }
    }
}
