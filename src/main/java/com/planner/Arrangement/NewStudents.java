package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import com.planner.Database.database;
public class NewStudents {
    ArrayList<Student> Students = new  ArrayList();
    String Sub_Code="null";
    public void fatchstudent(String SubCode) throws SQLException {
        Connection con;
        database db = new database();
        con = db.connection();
        Sub_Code = SubCode;

        PreparedStatement ps = con.prepareStatement("SELECT Enroll_no, Status FROM rawdata2 WHERE Sub_Code = ?");
        ps.setString(1, Sub_Code);
        ResultSet rs = ps.executeQuery();
        Student student;
        while (rs.next()) {
            student = new Student();
            student.Enroll_no = rs.getString("Enroll_no");
            student.Status = rs.getString("Status");
            student.SubCode = Sub_Code;
            Students.add(student);
        }
        Students.sort(Comparator.comparing(Student::getEnroll));
    }
    public ArrayList<Student> getStudents() {return Students;}
    public String getSub_Code() {return this.Sub_Code;}
    public int getLength(){return Students.size();}
}
