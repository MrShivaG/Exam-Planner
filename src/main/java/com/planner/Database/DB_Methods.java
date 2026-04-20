package com.planner.Database;

import com.planner.GUI.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_Methods {
    static database databaseobj = new database();
    public static Connection con;

    static {
        con = databaseobj.connection();
    }

    public DB_Methods() throws SQLException {
    }

    public static void main(String[] args) throws SQLException {

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
    public void deleteRoom(int room_no) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from class_room where room_no = ?");
        ps.setInt(1, room_no);
        ps.executeUpdate();
    }

    public void updatedata(int room_no,int rows,int columns) throws SQLException {
        PreparedStatement ps = con.prepareStatement("UPDATE class_room SET class_row = ?, class_column = ?, capacity = ? WHERE room_no = ?");

        ps.setInt(1,rows);
        ps.setInt(2,columns);
        ps.setInt(3,rows*columns);
        ps.setInt(4,room_no);
        ps.executeUpdate();
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



    public int totalroom() {
        if (this.con == null) return 0;

        int count = 0;
        try {
            PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM class_room");
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
        return count; // 0 ki jagah variable return karein
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
    public List<String[]> fetch_Arr_data() throws SQLException {
        PreparedStatement ps = con.prepareStatement("select * from arrangementDB");
        ResultSet rs = ps.executeQuery();
        List<String[]> arrangement = new ArrayList<>();
        while (rs.next()) {
            String arr_table_name = rs.getString("arr_table_name");
            String  arr_date = rs.getString("arr_date");
            String  capacity = rs.getString("capacity");
            String  arr_session = rs.getString("arr_session");
            String  students = rs.getString("students");
            arrangement.add(new String[]{arr_table_name, arr_date, capacity, arr_session,students});
        }
        return arrangement;
    }

    public List<Teacher> getTeachersByGender(String gender) throws SQLException {

        List<Teacher> list = new ArrayList<>();

        String query = "SELECT name, gender FROM teachers WHERE gender = ?";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, gender);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String gen = rs.getString("gender");

            list.add(new Teacher(name, gen));
        }

        return list;
    }
}