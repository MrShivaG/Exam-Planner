package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.planner.Database.database;
public class Students {
    ArrayList<String> students = new ArrayList<>();
    void fatchstudent(int index) throws SQLException {
        Connection con;

        database db = new database();
        con = db.connection();

        PreparedStatement ps = con.prepareStatement("SELECT * FROM students");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            students.add(rs.getString(index));
        }

    }
    ArrayList<String> getStudents(){return students;}
    int getLength(){return students.size();}
}
