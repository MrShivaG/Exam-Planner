package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import com.planner.Database.database;
public class Students {
    ArrayList<String> students = new ArrayList<>();


    String Sub_Code="null";
    void fatchstudent(String Sub_Code) throws SQLException {
        Connection con;

        database db = new database();
        con = db.connection();

        PreparedStatement ps = con.prepareStatement("SELECT Enroll_no FROM rawdata WHERE Sub_Code = ?");
        ps.setString(1, Sub_Code);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            students.add(rs.getString("Enroll_no"));
        }
        Collections.sort(students);

    }
    public void setSub_Code(String Sub_Code) throws SQLException {
        this.Sub_Code = Sub_Code;
    }
    public String getSub_Code() {return this.Sub_Code;}
    public ArrayList<String> getStudents(){return students;}
    public int getLength(){return students.size();}
}
