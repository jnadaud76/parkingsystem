package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, IS_REGULAR) values(?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=?, IS_REGULAR=? where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, t.IS_REGULAR, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
    //Compte les occurences d'une plaque d'immatriculation dans la table ticket au cours des deux derniers mois
    public static final String COUNT_VEHICLE_REG_NUMBER_FREQUENCY ="SELECT COUNT(VEHICLE_REG_NUMBER) FROM ticket WHERE IN_TIME > DATE_ADD(NOW(), INTERVAL -2 MONTH) AND VEHICLE_REG_NUMBER=?";
}
