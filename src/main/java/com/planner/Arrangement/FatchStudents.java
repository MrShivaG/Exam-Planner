package com.planner.Arrangement;
import com.planner.Database.database;

import java.sql.*;
import java.util.ArrayList;

public class FatchStudents {
    database  database = new database();
    Connection conn;

    ArrayList<Students> students = new ArrayList<>();

    public ArrayList<Students> fatchStudent() throws SQLException {
        int Sub_Count;
        try {
            conn = database.connection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM students Where 1=0");
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            Sub_Count = rsmd.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < Sub_Count; i++) {
//            students[i] = new Students();
//            students[i].fatchstudent(i+1);
            Students student = new Students();
            students.add(student);
            students.get(i).fatchstudent(i+1);

        }
        return students;
    }

//    public  Students[] getStudents() throws SQLException {
//        return students;
//    }
    public int getlength() throws SQLException {
        return students.size();
    }

}
