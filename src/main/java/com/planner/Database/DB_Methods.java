package com.planner.Database;

import com.planner.GUI.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


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
        PreparedStatement ps = con.prepareStatement("insert into class_room(room_no, class_row, class_column, capacity, availability) values(?, ?, ?, ?, ?)");
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
        return count;
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

    public void deleteArrRoom(String arr_table_name) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from arrangementDB where arr_table_name = ?");
        ps.setString(1, arr_table_name);
        ps.executeUpdate();
    }

    public void deleteArrangementGroup(String arr_group_name) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from arrgroups where GRP_names = ?");
        ps.setString(1, arr_group_name);
        ps.executeUpdate();
    }

    public void deleteArrangementtable(String arr_table_name) throws SQLException {
        PreparedStatement ps = con.prepareStatement("delete from "+arr_table_name);
        ps.setString(1, arr_table_name);
        ps.executeUpdate();
    }

    public List<String[]> fetch_groups_names() throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM arrgroups");
        ResultSet rs = ps.executeQuery();
        List<String[]> arr_group_names = new ArrayList<>();
        while (rs.next()) {
            String arr_groups = rs.getString("GRP_names");
            arr_group_names.add(new String[]{arr_groups});
        }
        return arr_group_names;
    }


    public List<String[]> fetch_group_tables(String arr_group_name) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select * from "+arr_group_name);
        ResultSet rs = ps.executeQuery();
        List<String[]> arrangement = new ArrayList<>();
        while (rs.next()) {
            String arr_table_name = rs.getString("arr_table_name");
            String room_no = rs.getString("room_no");
            String  arr_session = rs.getString("arr_session");
            String  arr_date = rs.getString("arr_date");
            String  capacity = rs.getString("capacity");
            String  students = rs.getString("student");
            String range_table = rs.getString("range_table");
            String rows_room   = rs.getString("rows_room");
            String faculty1 = null;
            String faculty = null;
            try {
                faculty1 = rs.getString("faculty_male");
            } catch (SQLException e) {
                try {
                    faculty1 = rs.getString("faculty1");
                } catch (SQLException ignored) {}
            }
            try {
                faculty = rs.getString("faculty_female");
            } catch (SQLException e) {
                try {
                    faculty = rs.getString("faculty");
                } catch (SQLException ignored) {}
            }
            arrangement.add(new String[]{arr_table_name,room_no, arr_date, capacity, arr_session,students,  range_table, rows_room, faculty1, faculty });
        }
        return arrangement;
    }

    public List<String[]> fetchRgpvSubjects() throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT subject_code, subject_name FROM rgpv_subjects ORDER BY subject_code"
        );
        ResultSet rs = ps.executeQuery();
        List<String[]> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new String[]{
                    rs.getString("subject_code"),
                    rs.getString("subject_name")
            });
        }
        return list;
    }

    public String[] fetchDateAndSession(String tableName) {
        String[] result = new String[2]; // [date, session]
        try {
            List<String> groups = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM arrgroups")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        groups.add(rs.getString(1));
                    }
                }
            }
            for (String grp : groups) {
                String query = "SELECT arr_date, arr_session FROM `" + grp + "` WHERE arr_table_name = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, tableName);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            result[0] = rs.getString("arr_date");
                            result[1] = rs.getString("arr_session");
                            return result;
                        }
                    }
                } catch (SQLException ignored) {
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching date/session for " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    public static Set<String> fetchBusyTeachers(String date, String session) {
        Set<String> busy = new HashSet<>();
        if (con == null || date == null || session == null) return busy;
        try {
            List<String> groups = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM arrgroups")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        groups.add(rs.getString("GRP_names"));
                    }
                }
            }
            for (String grp : groups) {
                String query = "SELECT * FROM `" + grp + "` WHERE arr_date = ? AND arr_session = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, date);
                    ps.setString(2, session);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String f1 = null;
                            try {
                                f1 = rs.getString("faculty_male");
                            } catch (SQLException e) {
                                try {
                                    f1 = rs.getString("faculty1");
                                } catch (SQLException ignored) {}
                            }
                            String f = null;
                            try {
                                f = rs.getString("faculty_female");
                            } catch (SQLException e) {
                                try {
                                    f = rs.getString("faculty");
                                } catch (SQLException ignored) {}
                            }
                            if (f1 != null && !f1.trim().isEmpty() && !f1.equalsIgnoreCase("null")) {
                                busy.add(f1.trim());
                            }
                            if (f != null && !f.trim().isEmpty() && !f.equalsIgnoreCase("null")) {
                                busy.add(f.trim());
                            }
                        }
                    }
                } catch (SQLException ignored) {
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching busy teachers: " + e.getMessage());
        }
        return busy;
    }

}
