package com.planner.Arrangement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
        //ArrayList<NewStudents> newStudents=new ArrayList<>();
        FatchStudentsV2 FSV2=new FatchStudentsV2();
        ArrayList<NewStudents> newStudents = FSV2.fatchStudent();

        ArrayList<Student> students22=new ArrayList<>();
        for(NewStudents s:newStudents){
            students22.addAll(s.getStudents());
        }
        //System.out.println(students22);
        for (Student s:students22){
            System.out.println(s.SubCode);
        }
        //System.out.println(newStudents.get(0).getStudents().getFirst().SubCode);


//        ArrayList<Student> students=new ArrayList<>();
//        for(NewStudents s:newStudents){
//            students.addAll(s.getStudents());
//        }
        ArrayList<String> rooms=new ArrayList<>();

        rooms = fetch_group_tables(grpname);
        SeatAllocator seatAllocator=new SeatAllocator();
            for(String t:rooms){
                PreparedStatement ps003 =conn1.prepareStatement("SELECT rows_room FROM "+grpname+" WHERE arr_table_name=?");
                ps003.setString(1,t);
                ResultSet rs003 = ps003.executeQuery();
                int r=0;
                while(rs003.next()){
                    r = rs003.getInt("rows_room");
                }
                Map<Integer, String> map =SeatAllocator.getBestSeats(conn,t, r,SubCode);
                if(map.isEmpty()){
                    System.out.println(t+" is empty ");
                    continue;

                }else {
                    System.out.println(t+" have "+map.size()+" seats");
                }
                System.out.println(r);
                System.out.println(map);
                for(Map.Entry<Integer, String> e:map.entrySet()){
                    for(NewStudents n:newStudents){
                       // System.out.println(newStudents.size());
                       // System.out.println(newStudents.get(i).getStudents().getFirst().SubCode);
                        try {
                            if (n.getStudents().isEmpty()){
                                continue;
                            }
                            if(n.getStudents().getFirst().SubCode.equals(e.getValue())){
                                PreparedStatement ps002 = conn.prepareStatement("UPDATE "+t+" SET Enroll_no=?, SubCode=?,Status=? WHERE ID=?");
                                ps002.setString(1, n.getStudents().getFirst().Enroll_no);
                                ps002.setString(2, n.getStudents().getFirst().SubCode);
                                ps002.setString(3, n.getStudents().getFirst().Status);
                                ps002.setInt(4, e.getKey());
                                ps002.executeUpdate();
                                System.out.println("added: "+n.getStudents().getFirst().Enroll_no+t);
                                n.removeone();
//                                if (n.getStudents().isEmpty()){
//                                    newStudents.remove(n);
//                                }
                                if (newStudents.isEmpty()){
                                    break;
                                }

                            }
                        } catch (Exception ex) {
                            System.out.println("via catch "+ex.getMessage());
                            System.out.println(ex);
                        }
                    }
                }
            }
        ArrayList<Student> students=new ArrayList<>();
        for(NewStudents s:newStudents){
            students.addAll(s.getStudents());
        }
        System.out.println("remain");
        for(Student s:students){
            System.out.println(s.SubCode);
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

    public void updateSeatingV2(List<String> subCodes, String grpname) throws Exception {

        // =========================
        // VALIDATE TABLE NAME
        // =========================

        if (!grpname.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Invalid group table name");
        }

        // =========================
        // FETCH STUDENTS
        // =========================

        FatchStudentsV2 fsv2 = new FatchStudentsV2();
        ArrayList<NewStudents> newStudents = fsv2.fatchStudent();

        // =========================
        // GROUP STUDENTS BY SUBCODE
        // =========================

        Map<String, Queue<Student>> studentsBySubCode = new HashMap<>();

        for (NewStudents ns : newStudents) {

            for (Student s : ns.getStudents()) {

                studentsBySubCode
                        .computeIfAbsent(s.SubCode, k -> new LinkedList<>())
                        .offer(s);
            }
        }

        // =========================
        // FETCH ROOMS
        // =========================

        ArrayList<String> rooms = fetch_group_tables(grpname);

        // =========================
        // PRELOAD ROOM ROWS
        // =========================

        Map<String, Integer> roomRows = new HashMap<>();

        String roomQuery =
                "SELECT rows_room FROM " + grpname + " WHERE arr_table_name=?";

        try (PreparedStatement ps = conn1.prepareStatement(roomQuery)) {

            for (String room : rooms) {

                if (!room.matches("[a-zA-Z0-9_]+")) {
                    throw new IllegalArgumentException("Invalid room table name");
                }

                ps.setString(1, room);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        roomRows.put(room, rs.getInt("rows_room"));
                    } else {
                        throw new RuntimeException("Room metadata not found: " + room);
                    }
                }
            }
        }

        // =========================
        // START TRANSACTION
        // =========================

        conn.setAutoCommit(false);

        String updateQuery =
                "UPDATE %s " +
                        "SET Enroll_no=?, SubCde=?, Branch=? " +
                        "WHERE ID=? AND (Enroll_no IS NULL OR Enroll_no='null')";

        int totalAllocated = 0;

        try {

            // =========================
            // PROCESS EACH ROOM
            // =========================

            for (String room : rooms) {

                int rows = roomRows.get(room);

                Map<Integer, String> bestSeats =
                        SeatAllocator.getBestSeats(
                                conn,
                                room,
                                rows,
                                subCodes
                        );

                if (bestSeats == null || bestSeats.isEmpty()) {
                    continue;
                }

                String finalUpdateQuery = String.format(updateQuery, room);

                try (PreparedStatement updatePs =
                             conn.prepareStatement(finalUpdateQuery)) {

                    // =========================
                    // ASSIGN SEATS
                    // =========================

                    for (Map.Entry<Integer, String> entry : bestSeats.entrySet()) {

                        int seatId = entry.getKey();
                        String requiredSubCode = entry.getValue();

                        Queue<Student> queue =
                                studentsBySubCode.get(requiredSubCode);

                        // no students left for this subcode
                        if (queue == null || queue.isEmpty()) {
                            continue;
                        }

                        Student student = queue.poll();

                        if (student == null) {
                            continue;
                        }

                        updatePs.setString(1, student.Enroll_no);
                        updatePs.setString(2, student.SubCode);
                        updatePs.setString(3, student.branch);
                        updatePs.setInt(4, seatId);

                        updatePs.addBatch();

                        totalAllocated++;
                    }

                    updatePs.executeBatch();
                }
            }

            // =========================
            // COMMIT
            // =========================

            conn.commit();

        } catch (Exception e) {

            conn.rollback();
            throw e;

        } finally {

            conn.setAutoCommit(true);
        }

        // =========================
        // REMAINING STUDENTS
        // =========================

        ArrayList<Student> remaining = new ArrayList<>();

        for (Queue<Student> q : studentsBySubCode.values()) {
            remaining.addAll(q);
        }

        // =========================
        // LOG RESULTS
        // =========================

        System.out.println("Total Allocated: " + totalAllocated);

        if (!remaining.isEmpty()) {

            System.out.println("Remaining Students:");

            for (Student s : remaining) {
                System.out.println(
                        s.Enroll_no + " | " +
                                s.SubCode + " | " +
                                s.branch
                );
            }
        }
    }


}
