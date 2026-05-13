package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.database;

public class Editor {
    ArrangementsDB arrangementsDB = new ArrangementsDB();
    Connection conn;
    {
        try {
            conn = arrangementsDB.connection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    database DB = new database();
    Connection conn1;
    {
        try {
            conn = arrangementsDB.connection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String,Integer> filterbackStudents(String grpname) throws SQLException {
        Map<String,Integer> map=new HashMap<>();
        ArrayList<String> rooms=new ArrayList<String>();
        for(String t:rooms){
            Pattern pattern = Pattern.compile("^[^_]+_(\\d+)_");
            Matcher matcher = pattern.matcher(t);
            String room_num="";
            if (matcher.find()) {
                room_num = matcher.group(1);
            }
            PreparedStatement ps =conn.prepareStatement("SELECT Enroll_no FROM "+t+"Where status='Back'");
            ResultSet rs0 = ps.executeQuery();
            while(rs0.next()){
                map.put(rs0.getString("Enroll_no"), Integer.valueOf(room_num));
            }

        }
        return map;
    }


    public void updateSeating(String[] SubCode,String grpname) throws SQLException {
        ArrayList<NewStudents> newStudents=new ArrayList<>();
        FatchStudentsV2 FSV2=new FatchStudentsV2();
        newStudents = FSV2.fatchStudent();

        ArrayList<String> rooms=new ArrayList<>();
        rooms = fetch_group_tables(grpname);
        for(String t:rooms){
            ArrayList<Integer> seats=new ArrayList<>();
            PreparedStatement ps =conn.prepareStatement("SELECT id FROM "+t+"Where Enroll_no='null'");
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                seats.add(rs.getInt("id"));
            }

        }


    }
    public ArrayList<String> fetch_group_tables(String arr_group_name) throws SQLException {
        PreparedStatement ps = conn1.prepareStatement("select * from "+arr_group_name);
        ResultSet rs = ps.executeQuery();
        ArrayList<String> group_tables=new ArrayList<>();
        while (rs.next()) {
            group_tables.add(rs.getString("arr_table_name"));
        }
        return group_tables;
    }
    public void Deletestuedent(String Table, String stu) throws SQLException {
        PreparedStatement ps = conn1.prepareStatement("Update "+Table+" set Enroll_no='Null' where Enroll_no='"+stu+"'");
        ps.executeUpdate();
    }
    public void AddStudent(String Table, String stu, String Status, String Subcode) throws SQLException {
        PreparedStatement ps = conn1.prepareStatement("UPDATE " + Table +" SET Status=? WHERE Enroll_no=?");
        ps.setString(1, Status);
        ps.setString(2, stu);
        ps.executeUpdate();
    }

}
