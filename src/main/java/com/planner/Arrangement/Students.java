package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.planner.Database.database;
public class Students {
    ArrayList<String> students = new ArrayList<>();
    void fatchstudent(String Sub_Code) throws SQLException {
        Connection con;
        try {
            database db = new database();
            con = db.connection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PreparedStatement ps = con.prepareStatement("SELECT ? FROM class_room");
        ps.setString(1, Sub_Code);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            students.add(rs.getString(Sub_Code));
        }

    }
    ArrayList<String> getStudents(){return students;}
    int getLength(){return students.size();}
}
