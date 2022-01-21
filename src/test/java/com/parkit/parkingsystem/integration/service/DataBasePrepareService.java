package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;

        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            ps = connection
                    .prepareStatement("update parking set available = true");
            ps.execute();


            //clear ticket entries;
            ps2 = connection
                    .prepareStatement("truncate table ticket");
            ps2.execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closePreparedStatement(ps2);
            dataBaseTestConfig.closePreparedStatement(ps);
            dataBaseTestConfig.closeConnection(connection);
        }
    }


}
