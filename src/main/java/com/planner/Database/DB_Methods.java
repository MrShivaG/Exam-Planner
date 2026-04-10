package com.planner.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;

public class DB_Methods {
    static database databaseobj = new database();
    Connection con = databaseobj.connection();

    public DB_Methods() throws SQLException {
    }

    public static void main(String[] args) {
        System.out.println(databaseobj.connectDB());
    }

    public void insertData(int room_no, int rows, int column, boolean availability) throws SQLException {
        PreparedStatement ps = con.prepareStatement("insert into class_room(room_no, class_row, class_column, capacity, availability) values(?, ?, ?, ?, ?)");
        ps.setInt(1, room_no);
        ps.setInt(2, rows);
        ps.setInt(3, column);
        ps.setInt(4, rows*column);
        ps.setBoolean(5, availability);
        ps.executeUpdate();
        con.close();
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
}