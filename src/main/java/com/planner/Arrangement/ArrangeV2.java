package com.planner.Arrangement;

import java.sql.*;
import java.util.ArrayList;

import com.mysql.cj.Session;
import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.Database.database;
public class ArrangeV2 {
    FatchStudents fatchstudents =new FatchStudents();

    public ArrayList<String> arrange(int[] classroomsArray ,String Date, String ArrName, String Sem,String Session11, String ORGID) throws SQLException {

        ArrangementsDB arrangementsDB = new ArrangementsDB();
        Connection conn = arrangementsDB.connection();
        boolean SetNull = true;

        database Database = new database();
        Connection conn1 = Database.connection();

        ArrayList<String> ReturnStatement = new ArrayList<>();

        ArrayList<Students> students = fatchstudents.fatchStudent();
        int length = fatchstudents.getlength();

        ClassRooms classrooms = new ClassRooms();

        classrooms.addClassroom(classroomsArray);

        ArrayList<Integer> classes = new ArrayList<>();
        classes = classrooms.getClassrooms();
        int classesLength = classrooms.getLength();

        DB_Methods db = new DB_Methods();
        int currentClassIndex = 0;
        String grpname =ORGID+ArrName+"_"+Sem+"_"+Session11;
        String grpquery ="CREATE TABLE "+grpname+" (arr_table_name varchar(50), range_table varchar(50),room_no varchar(20), arr_date varchar(50), arr_session varchar(10), capacity int(20), student int(20), faculty1 varchar(30), faculty varchar(30), rows_room varchar(10))";
        //arr_table_name varchar(100), range_table varchar(50), arr_date varchar(100), arr_session varchar(50), capacity int(50), student int(50)
        PreparedStatement ps002 = conn1.prepareStatement(grpquery);
        ps002.executeUpdate();
        PreparedStatement ps003 = conn1.prepareStatement("INSERT INTO arrgroups value('"+grpname+"')");
        ps003.executeUpdate();

        while(true){

            String Table_name = ORGID+"_GRP_"+classes.get(currentClassIndex)+"_"+ArrName+"_"+ Session11+"_"+Sem;
            int[] RC = db.fetchRowColumn(classes.get(currentClassIndex));

            int totalstu=0;
            PreparedStatement ps001 = conn.prepareStatement("CREATE TABLE "+Table_name+" (Row1 VARCHAR(100), SubCode Varchar(50), Status varchar(100))");
            ps001.executeUpdate();
            System.out.println("Table created");

            String query2 ="Insert into "+Table_name+" values(?,?,?)";

            int index=0;
            for(int i=0;i<RC[1];i++){

                System.out.println(query2);
                if(RC[0]%2==0){
                    index=(index+3)%2; //change the index if rows are even
                }
                for(int j=1;j<=RC[0];j++){
                    System.out.println(index);
                    PreparedStatement ps = conn.prepareStatement(query2);
                    try {

                        ps.setString(1,students.get(index).getStudents().get(0));
                        ps.setString(2,"null");
                        ps.setString(3,"null");
                        System.out.println(ps.toString());
                        students.get(index).getStudents().remove(0);

                        totalstu++;

                        if (students.get(index).getStudents().isEmpty()){
                            students.remove(index);
                            if (index==0){
                                index++;
                            }
                        }
                        if (students.isEmpty()){
                            System.out.println("No students Left 1");
                            break;
                        }
                    }catch (IndexOutOfBoundsException e){
                        if (students.isEmpty()){
                            System.out.println("No students Left 2");
                            break;
                        }
                        System.out.println("nnnnul");
                        ps.setString(1, "ull");
                        ps.setString(2, "Null");
                        ps.setString(3, "Null");
                        System.out.println(ps.toString());


                    }
                    index = (index+3)%2;
                    ps.executeUpdate();

                }
                //System.out.println(ps.toString());
                //ps.executeUpdate();
            }
            int totalCap = RC[0]*RC[1];
            ReturnStatement.add(Date+"_"+classes.get(currentClassIndex));
            PreparedStatement ps11 = conn1.prepareStatement("Insert into "+grpname+" values (?,NULL,?,?,?,?,?,NULL,NULL,?)");
            ps11.setString(1, Table_name);
            ps11.setString(2, String.valueOf(classes.get(currentClassIndex)));
            ps11.setString(3, Date);
            ps11.setString(4, Session11);
            ps11.setString(5, String.valueOf(totalCap));
            ps11.setString(6, String.valueOf(totalstu));
            ps11.setString(7, String.valueOf(RC[1]));
            ps11.executeUpdate();


            if (students.isEmpty()){
                System.out.println("No students Left 3");
                break;
            }
            if(classesLength==currentClassIndex+1){
                break;
            }
            currentClassIndex++;

        }
        return ReturnStatement;

    }
}
