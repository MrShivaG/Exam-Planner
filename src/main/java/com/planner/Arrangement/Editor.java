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
        conn1 = DB.connection();
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


    public void updateSeating(List<String> SubCode,String grpname) throws Exception {
        ArrayList<NewStudents> newStudents=new ArrayList<>();
        FatchStudentsV2 FSV2=new FatchStudentsV2();
        newStudents = FSV2.fatchStudent();

//        ArrayList<Student> students=new ArrayList<>();
//        for(NewStudents s:newStudents){
//            students.addAll(s.getStudents());
//        }
        ArrayList<String> rooms=new ArrayList<>();

        rooms = fetch_group_tables(grpname);
        SeatAllocator seatAllocator=new SeatAllocator();
        while(newStudents.size()>0){
            for(String t:rooms){
                PreparedStatement ps003 =conn1.prepareStatement("SELECT rows_room FROM "+grpname+" WHERE arr_table_name=?");

                ps003.setString(1,t);
                ResultSet rs003 = ps003.executeQuery();
                int r=0;
                while(rs003.next()){
                    r = rs003.getInt("rows_room");
                }
                Map<Integer, String> map =SeatAllocator.getBestSeats(conn,t, r,SubCode);
                for(Map.Entry<Integer, String> e:map.entrySet()){
                    for(int i=0;i<newStudents.size();i++){
                        if(newStudents.get(i).Students.get(0).SubCode==e.getValue()){
                            PreparedStatement ps002 = conn.prepareStatement("UPDATE "+t+" SET Enroll_no=?, SubCde=?,Branch=? WHERE ID=?");
                            ps002.setString(1, newStudents.get(i).Students.getFirst().Enroll_no);
                            ps002.setString(2, newStudents.get(i).Students.getFirst().SubCode);
                            ps002.setString(3, newStudents.get(i).Students.getFirst().branch);
                            ps002.setInt(4, e.getKey());
                            ps002.executeUpdate();
                            newStudents.get(i).Students.removeFirst();
                            if (newStudents.get(i).Students.size()==0){
                                newStudents.remove(i);
                            }
                            if (newStudents.size()==0){
                                break;
                            }

                        }
                    }
                }
            }
        ArrayList<Student> students=new ArrayList<>();
        for(NewStudents s:newStudents){
            students.addAll(s.getStudents());
        }
            System.out.println(students);
        }


    }
    public boolean AddnewStudents(Connection con,String[] SubCode,String table) throws SQLException {
        return true;
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
