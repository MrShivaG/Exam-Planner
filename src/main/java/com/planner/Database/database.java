package com.planner.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    private static final String URL = "jdbc:mysql://localhost:3306/exam_planner";
    //10.225.181.20
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Kkc@#2007";

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
        Connection con = connection();
        return "Database connected successfully.";
    }
    public static Connection connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return con;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

}