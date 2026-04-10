package com.planner.Arrangement;

import java.util.ArrayList;

public class ClassRooms {
    ArrayList<Integer> Classrooms = new ArrayList<>();
    public void addClassroom(int[] room_no){
        for(int i=0;i<room_no.length;i++){
            Classrooms.add(room_no[i]);
        }
    }

    public ArrayList<Integer> getClassrooms(){return Classrooms;}
    public int getLength(){return Classrooms.size();}

}
