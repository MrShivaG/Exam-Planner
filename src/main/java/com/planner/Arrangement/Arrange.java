package com.planner.Arrangement;

import java.sql.*;
import java.util.ArrayList;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
public class Arrange {
    FatchStudents fatchstudents =new FatchStudents();

    public void arrange(String Date) throws SQLException {
        ArrangementsDB arrangementsDB = new ArrangementsDB();
        Connection conn = arrangementsDB.connection();

        ArrayList<Students> students = fatchstudents.fatchStudent();
        int length = fatchstudents.getlength();

        ClassRooms classrooms = new ClassRooms();
        ArrayList<Integer> classes = new ArrayList<>();
        classes = classrooms.getClassrooms();
        int classesLength = classrooms.getLength();

        DB_Methods db = new DB_Methods();
        int currentClassIndex = 0;
        int currentStudentIndex = 0;
        int curreentStudentSetIndex = 0;



        while(true){

            int[] RC = db.fetchRowColumn(classes.get(currentClassIndex));
            int capacity = RC[0]*RC[1];
            int filledseats=0;
            String query ="Insert ? values(?";
            for(int i=1;i<RC[0];i++){
                query = query+" ,?";
            }
            query = query+");";
            createTable(conn,RC[1],"12_1_105");
            int index=0;
            for(int i=0;i<RC[1];i++){
                PreparedStatement ps = conn.prepareStatement(query);
                for(int j=0;j<RC[0];j++){
                    try {
                        ps.setString(j,students.get(index).getStudents().get(0));
                        students.get(index).getStudents().remove(0);

                    }catch (IndexOutOfBoundsException e){
                        ps.setString(j,"Null");
                    }
                    if (students.get(index).getStudents().isEmpty()){
                        students.remove(index);
                    }
                    if(index==0){
                        index++;
                    }
                    else{
                        index--;
                    }


                }
                ps.executeUpdate();
            }
            if(classesLength==currentClassIndex){
                break;
            }
            currentClassIndex++;

        }

    }
    void createTable(Connection conn,int Column,String Date) throws SQLException {

        PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (Row1 VARCHAR(100))");
        ps.setString(1, Date);
        ps.executeUpdate();
        for(int i=2;i<Column+1;i++){
            PreparedStatement ps1 = conn.prepareStatement("ALTER TABLE ? add COLUMN ? Varchar(100)");
            ps1.setString(1, Date);
            String st="Row"+i;
            ps1.setString(2, st);
            ps1.executeUpdate();
        }
    }


}
