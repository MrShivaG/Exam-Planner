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

    public static boolean autoAssignTeachers(List<Room> selectedRooms, String date, String session) {

        try {

            DB_Methods db = new DB_Methods();

            List<Teacher> maleList =
                    db.getTeachersByGender("Male");

            List<Teacher> femaleList =
                    db.getTeachersByGender("Female");

            // Filter out busy teachers
            Set<String> busyTeachers = DB_Methods.fetchBusyTeachers(date, session);

            List<Teacher> availableMale = new ArrayList<>();
            for (Teacher t : maleList) {
                if (t.getName() != null && !busyTeachers.contains(t.getName().trim())) {
                    availableMale.add(t);
                }
            }

            List<Teacher> availableFemale = new ArrayList<>();
            for (Teacher t : femaleList) {
                if (t.getName() != null && !busyTeachers.contains(t.getName().trim())) {
                    availableFemale.add(t);
                }
            }

            Collections.shuffle(availableMale);
            Collections.shuffle(availableFemale);

            roomTeachers.clear();

            if (availableMale.size() < selectedRooms.size()
                    || availableFemale.size() < selectedRooms.size()) {

                Notification.message(
                        "Not enough available teachers for this Date & Session."
                );

                return false;
            }

            roomTeachers.clear();

            for (int i = 0; i < selectedRooms.size(); i++) {

                Room room = selectedRooms.get(i);

                Teacher male = availableMale.get(i);
                Teacher female = availableFemale.get(i);

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
