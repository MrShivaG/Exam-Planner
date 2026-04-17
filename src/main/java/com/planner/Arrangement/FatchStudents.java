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
        conn = database.connection();
        ArrayList<String> result = new ArrayList<>();
        PreparedStatement ps4 =conn.prepareStatement("SELECT DISTINCT Sub_code from rawdata");
        ResultSet rs2 = ps4.executeQuery();
        while (rs2.next()) {
            result.add(rs2.getString("Sub_code"));
        }

        for (String Sub_Code : result) {

//            students[i] = new Students();
//            students[i].fatchstudent(i+1);
            Students student = new Students();
            student.setSub_Code(Sub_Code);
            student.fatchstudent(Sub_Code);
            students.add(student);
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
