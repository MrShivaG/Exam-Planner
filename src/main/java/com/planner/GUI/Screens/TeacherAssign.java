package com.planner.GUI.Screens;

import com.planner.Database.DB_Methods;
import com.planner.GUI.Notification;
import com.planner.GUI.Room;
import com.planner.GUI.Teacher;

import java.util.*;

public class TeacherAssign {

    private static final Map<Integer, List<Teacher>> roomTeachers =
            new HashMap<>();

    public static Map<Integer, List<Teacher>> getRoomTeachers() {

        return roomTeachers;
    }

    public static boolean autoAssignTeachers(List<Room> selectedRooms) {

        try {

            DB_Methods db = new DB_Methods();

            List<Teacher> maleList =
                    db.getTeachersByGender("Male");

            List<Teacher> femaleList =
                    db.getTeachersByGender("Female");

            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);

            roomTeachers.clear();

            if (maleList.size() < selectedRooms.size()
                    || femaleList.size() < selectedRooms.size()) {

                Notification.message(
                        "Not enough teachers available."
                );

                return false;
            }

            roomTeachers.clear();

            for (int i = 0; i < selectedRooms.size(); i++) {

                Room room = selectedRooms.get(i);

                Teacher male = maleList.get(i);
                Teacher female = femaleList.get(i);

                List<Teacher> list = new ArrayList<>();

                list.add(male);
                list.add(female);

                roomTeachers.put(room.getRoomNo(), list);
            }

            return true;

        } catch (Exception e) {

            Notification.message("Unable to assign teachers");

            return false;
        }
    }
}
