package com.planner.GUI.Screens;

import com.planner.Database.DB_Methods;
import com.planner.GUI.Notification;
import com.planner.GUI.Room;
import com.planner.GUI.Teacher;

import java.util.*;

public class TeacherAssign {

    public static Map<Integer, List<Teacher>> roomTeachers = new HashMap<>();

    public static boolean autoAssignTeachers(List<Room> selectedRooms) {
        try {
            DB_Methods db = new DB_Methods();

            List<Teacher> maleList = db.getTeachersByGender("Male");
            List<Teacher> femaleList = db.getTeachersByGender("Female");

            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);

            int rooms = selectedRooms.size();

            if (maleList.size() < selectedRooms.size()
                    || femaleList.size() < selectedRooms.size()) {

                Notification.message(
                        "Not enough teachers available."
                );

                return false;
            }

            for (int i = 0; i < selectedRooms.size(); i++) {

                Room room = selectedRooms.get(i);

                Teacher male = maleList.get(i);
                Teacher female = femaleList.get(i);

                List<Teacher> list = new ArrayList<>();
                list.add(male);
                list.add(female);

                roomTeachers.put(room.getRoomNo(), list);
            }

            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);

            roomTeachers.clear();


            for (int i = 0; i < rooms; i++) {
                Room room = selectedRooms.get(i);

                List<Teacher> list = new ArrayList<>();
                list.add(maleList.get(i));
                list.add(femaleList.get(i));

                roomTeachers.put(room.getRoomNo(), list);
            }

            return true;

        } catch (Exception e) {
            Notification.message("Error assigning teachers");
            return false;
        }
    }

}
