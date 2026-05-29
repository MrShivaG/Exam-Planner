package com.planner.Arrangement;

import java.sql.*;
import java.util.ArrayList;

import com.mysql.cj.Session;
import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.Database.database;
public class ArrangeV2 {
    FatchStudents fatchstudents =new FatchStudents();

    public String arrange(
            int[] classroomsArray ,
            ArrayList<String> SubCode,
            ArrayList<ArrayList<String>> OSubcode,
            String Date,
            String ArrName,
            String Sem,
            String Session11,
            String ORGID,
            String ClgName,
            String Programe,
            String Time,
            Boolean RangL,
            Boolean Alt,
            Boolean AltL
    ) throws Exception {
        ArrangementsDB arrangementsDB = new ArrangementsDB();
        Connection conn = arrangementsDB.connection();

        database Database = new database();
        Connection conn1 = Database.connection();


        ArrayList<Students> students = fatchstudents.fatchStudent();
        int length = fatchstudents.getlength();
        FatchStudentsV2 FSV2 = new FatchStudentsV2();
        ArrayList<NewStudents> studentsV2 = FSV2.fatchStudent();

        ClassRooms classrooms = new ClassRooms();

        classrooms.addClassroom(classroomsArray);

        ArrayList<Integer> classes = new ArrayList<>();
        classes = classrooms.getClassrooms();
        int classesLength = classrooms.getLength();

        DB_Methods db = new DB_Methods();
        int currentClassIndex = 0;
        String grpname =ORGID+"_"+ArrName+"_"+Sem+"_"+Session11;
        String grpquery ="CREATE TABLE "+grpname+" (arr_table_name varchar(50),room_no varchar(20), capacity int(20), student int(20), faculty1 varchar(100), faculty2 varchar(100), rows_room varchar(10))";
        //arr_table_name varchar(100), range_table varchar(50), arr_date varchar(100), arr_session varchar(50), capacity int(50), student int(50)
        PreparedStatement ps002 = conn1.prepareStatement(grpquery);
        ps002.executeUpdate();
        PreparedStatement ps003 = conn1.prepareStatement("INSERT INTO arrgroups value(?,?,?,?,?,?,?)");
        ps003.setString(1, grpname);
        ps003.setString(2, ClgName);
        ps003.setString(3, Programe);
        ps003.setString(4, Time);
        ps003.setString(6, Session11);
        ps003.setString(5,grpname+"_Rang");
        ps003.setString(7,Date);
        ps003.executeUpdate();
        while(true){

            String Table_name = ORGID+"_GRP_"+classes.get(currentClassIndex)+"_"+ArrName+"_"+ Session11+"_"+Sem;
            int[] RC = db.fetchRowColumn(classes.get(currentClassIndex));

            int totalstu=0;
            PreparedStatement ps001 = conn.prepareStatement("CREATE TABLE "+Table_name+" (Id INT AUTO_INCREMENT PRIMARY KEY, Enroll_no VARCHAR(100), SubCode Varchar(50), Status varchar(100))");
            ps001.executeUpdate();
            System.out.println("Table created");

            String query2 ="INSERT INTO "+Table_name+" (Enroll_no, SubCode, Status) VALUES (?,?,?)";

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

                        ps.setString(1,studentsV2.get(index).getStudents().getFirst().Enroll_no);
                        ps.setString(2,studentsV2.get(index).getStudents().getFirst().SubCode);
                        ps.setString(3,studentsV2.get(index).getStudents().getFirst().Status);
                        System.out.println(ps.toString());
                        studentsV2.get(index).getStudents().remove(0);

                        totalstu++;

                        if (studentsV2.get(index).getStudents().isEmpty()){
                            studentsV2.remove(index);
                            if (index==0){
                                index++;
                            }
                        }
//                        if (studentsV2.isEmpty()){
//                            System.out.println("No students Left 1");
//                            break;
//                        }
                    }catch (IndexOutOfBoundsException e){
//                        if (studentsV2.isEmpty()){
//                            System.out.println("No students Left 2");
//                            break;
//                        }
                        System.out.println("nnnnul");
                        ps.setString(1, "Null");
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

            PreparedStatement ps11 = conn1.prepareStatement("Insert into "+grpname+" values (?,?,?,?,NULL,NULL,?)");
            ps11.setString(1, Table_name);
//            ps11.setString(2, Table_name+"_Range");
            ps11.setString(2, String.valueOf(classes.get(currentClassIndex)));
//            ps11.setString(4, Date);
//            ps11.setString(5, Session11);
            ps11.setString(3, String.valueOf(totalCap));
            ps11.setString(4, String.valueOf(totalstu));
            ps11.setString(5, String.valueOf(RC[0]));
            ps11.executeUpdate();


            if (RangL==true){
                EnrollmentRangeBuilder builder = new EnrollmentRangeBuilder();
                builder.buildRanges(conn,Table_name, grpname+"_Rang", String.valueOf(classes.get(currentClassIndex)),Table_name);
            } else if (RangL==false) {
                RangeGenerator rangeGenerator = new RangeGenerator();
                rangeGenerator.generateRangeTable(conn,Table_name,grpname+"_Range");
            }

            if (studentsV2.isEmpty()){
                System.out.println("No students Left 3");
                break;
            }
            if(classesLength==currentClassIndex+1){
                System.out.println("index/length: "+currentClassIndex+"/"+classesLength);


                break;
            }
            currentClassIndex++;

        }
        return grpname;

    }
}
