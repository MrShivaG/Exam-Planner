package com.planner.Arrangement;

import java.sql.*;
import java.util.ArrayList;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.Database.database;
public class Arrange {
    FatchStudents fatchstudents =new FatchStudents();

    public ArrayList<String> arrange(int[] classroomsArray ,String Date) throws SQLException {
        ArrangementsDB arrangementsDB = new ArrangementsDB();
        Connection conn = arrangementsDB.connection();

        database Database = new database();
        Connection conn1 = Database.connection();

        ArrayList<String> ReturnStatement = new ArrayList<>();

        System.out.println("ArrangeDB: "+arrangementsDB.connectDB());

        ArrayList<Students> students = fatchstudents.fatchStudent();
        int length = fatchstudents.getlength();

        ClassRooms classrooms = new ClassRooms();

        classrooms.addClassroom(classroomsArray);

        ArrayList<Integer> classes = new ArrayList<>();
        classes = classrooms.getClassrooms();
        int classesLength = classrooms.getLength();

        DB_Methods db = new DB_Methods();
        int currentClassIndex = 0;


        while(true){

            String Table_name = Date+"_"+classes.get(currentClassIndex);
            int[] RC = db.fetchRowColumn(classes.get(currentClassIndex));

            String query ="Insert into "+Table_name+" values(?";
            for(int i=1;i<RC[0];i++){
                query = query+" ,?";
            }
            query = query+");";

            createTable(conn,RC[0],Table_name);


            int index=0;
            for(int i=0;i<RC[1];i++){
                PreparedStatement ps = conn.prepareStatement(query);
                System.out.println(query);
                if(RC[0]%2==0){
                    index=(index+3)%2; //change the index if rows are even
                }
                for(int j=1;j<=RC[0];j++){
                    System.out.println(index);
                    try {
                        ps.setString(j,students.get(index).getStudents().get(0));
                        students.get(index).getStudents().remove(0);



                        if (students.get(index).getStudents().isEmpty()){
                            students.remove(index);
                            if (index==0){
                                index++;
                            }
                        }
                    }catch (IndexOutOfBoundsException e){
                        ps.setString(j,"Null");
                    }
                    index = (index+3)%2;

                }
                System.out.println(ps.toString());
                ps.executeUpdate();
            }
            //int totalCap = db.;
            ReturnStatement.add(Date+"_"+classes.get(currentClassIndex));

            if (students.isEmpty()){
                System.out.println("No students Left");
                break;
            }
            if(classesLength==currentClassIndex+1){
                break;
            }
            currentClassIndex++;

        }

        return ReturnStatement;

    }
    void createTable(Connection conn,int Column,String Date) throws SQLException {

        PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "+Date+" (Row1 VARCHAR(100))");
        //ps.setString(1, Date);
        ps.executeUpdate();
        System.out.println("Table created");
        for(int i=2;i<Column+1;i++){
            String st="Row"+i;
            PreparedStatement ps1 = conn.prepareStatement("ALTER TABLE "+Date+" add COLUMN "+st+" Varchar(100)");
            //ps1.setString(1, Date);
            //ps1.setString(2, st);
            ps1.executeUpdate();
            System.out.println("Columns created");
        }
    }


}
