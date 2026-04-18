package com.planner.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.planner.Database.DB_Methods.con;

public class ArrangementsDB {

    private static final String URL = "jdbc:mysql://10.225.181.20:3306/arrangements";
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

    public static List<List<String>> fetcharrData(String arr_table_name) throws SQLException {
        Connection con = connection();
        String query = "SELECT * FROM `" + arr_table_name + "`";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<List<String>> result = new ArrayList<>();

        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getString(i));
            }
            result.add(row);
        }

        return result;
    }

}