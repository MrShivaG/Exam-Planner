package com.planner.Arrangement;
import com.planner.Database.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FatchStudentsV2 {
    database  database = new database();
    Connection conn;

    ArrayList<NewStudents> students = new ArrayList<>();
    public ArrayList<NewStudents> fatchStudent() throws SQLException {
        int Sub_Count;
        conn = database.connection();
        ArrayList<String> result = new ArrayList<>();
        PreparedStatement ps4 =conn.prepareStatement("SELECT DISTINCT Sub_code from rawdata");
        ResultSet rs2 = ps4.executeQuery();
        while (rs2.next()) {
            result.add(rs2.getString("Sub_code"));
        }

        for (String Sub_Code : result) {
            NewStudents student = new NewStudents();
            student.fatchstudent(Sub_Code);
            students.add(student);
        }
        return students;

    }


    public int getlength() throws SQLException {
        return students.size();
    }

}
