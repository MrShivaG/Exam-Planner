package com.planner.Database;

import com.planner.GUI.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TeacherDB {

    public static List<Teacher> fetchTeachers() {

        List<Teacher> teachers = new ArrayList<>();

        try {

            Connection conn = database.connection();

            PreparedStatement ps =
                    conn.prepareStatement("SELECT * FROM teachers");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                teachers.add(
                        new Teacher(
                                rs.getString("name"),
                                rs.getString("gender")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teachers;
    }
}