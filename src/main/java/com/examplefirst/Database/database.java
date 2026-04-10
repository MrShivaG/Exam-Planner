package com.examplefirst.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    private static final String URL = "jdbc:mysql://192.168.1.169:3306/examplanner";
    private static final String USERNAME = "remote";
    private static final String PASSWORD = "root";

    //main method
    public static void main(String[] args) {
        try {
            Connection con = connection();
                System.out.println("Database connected successfully.");
            con.close();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }


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