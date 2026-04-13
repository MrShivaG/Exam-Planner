package com.planner.Database;

import com.mysql.cj.jdbc.CallableStatementWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Methods {
    static database databaseobj = new database();
    static Connection con;

    static {
        try {
            con = databaseobj.connection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DB_Methods() throws SQLException {
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(databaseobj.connectDB());
        System.out.println(totalroom());
        System.out.println(totalcapacity());
        List<int[]> rooms = fetchRowColumn();
        for (int i = 0; i < rooms.size(); i++) {
            int[] arr = rooms.get(i);
            System.out.println("Room: " + arr[0] + ", Rows: " + arr[1] + ", Columns: " + arr[2]);
        }
        con.close();
    }


    public static void insertData(int room_no, int rows, int column, boolean availability) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert ignore into class_room(room_no, class_row, class_column, capacity, availability) values(?, ?, ?, ?, ?)");
        ps.setInt(1, room_no);
        ps.setInt(2, rows);
        ps.setInt(3, column);
        ps.setInt(4, rows*column);
        ps.setBoolean(5, availability);
        ps.executeUpdate();

    }
    public void deleterow(int room_no) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from class_room where room_no = ?");
        ps.setInt(1, room_no);
        ps.executeUpdate();
        con.close();
    }

    public void updatedata(int room_no) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from class_room where room_no = ?");


    }

    public static List<int[]> fetchRowColumn() throws SQLException {
        PreparedStatement ps = con.prepareStatement("select * from class_room");
        ResultSet rs = ps.executeQuery();
        List<int[]> rooms = new ArrayList<>();
        while (rs.next()) {
            int roomNo = rs.getInt("room_no");
            int row = rs.getInt("class_row");
            int column = rs.getInt("class_column");
            rooms.add(new int[]{roomNo, row, column});
        }
        return rooms;
    }

    public int[] fetchRowColumn(int room_no) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select * from class_room where room_no = ?");
        ps.setInt(1, room_no);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int row = rs.getInt("class_row");
            int column = rs.getInt("class_column");
            return new int[]{row, column};
        }
        return new int[]{-1, -1};
    }



    public static int totalroom() throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS total_rooms FROM class_room");
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int total = rs.getInt("total_rooms");
            return total;
        }

        rs.close();
        ps.close();
       return -1;
    }

    public static int totalcapacity() throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT SUM(capacity) AS total_capacity FROM class_room");
        ResultSet rs= ps.executeQuery();
        if(rs.next()){
            int total = rs.getInt("total_capacity");
            return total;
        }
        rs.close();
        ps.close();
        return -1;
    }

}