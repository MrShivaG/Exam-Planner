package com.planner.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ArrangementsDB {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/arrangements";
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "root";


    public static String connectDB(){
        try {
            Connection con = connection();
            return "Database connected successfully.";
        } catch (SQLException e) {
            return "Connection failed: " + e.getMessage();
        }
    }
    public static Connection connection() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return con;
    }

}