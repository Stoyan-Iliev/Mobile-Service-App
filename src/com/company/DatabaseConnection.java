package com.company;

import com.company.repositories.ClientRepository;
import com.company.repositories.base.DataRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection createConnection(){
        Properties prop = new Properties();
        prop.setProperty("user", "root");
        prop.setProperty("password", "");

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mobile_services", prop);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}
