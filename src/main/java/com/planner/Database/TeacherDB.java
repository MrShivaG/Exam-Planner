package com.planner.Database;

import com.planner.GUI.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.planner.Database.DB_Methods.con;

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
                                rs.getInt("id"),        // id fix
                                rs.getString("name"),
                                rs.getString("gender"),
                                rs.getString("branch") != null ? rs.getString("branch") : "",
                                rs.getString("phone")  != null ? rs.getString("phone")  : "",
                                rs.getString("email")  != null ? rs.getString("email")  : ""
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teachers;
    }

    public static void addTeacher(
            String name,
            String gender,
            String branch,
            String phone,
            String email
    ) {

        try {

            Connection conn = database.connection();

            String query = """
                INSERT INTO teachers
                (name, gender, branch, phone, email)
                VALUES (?, ?, ?, ?, ?)
                """;

            PreparedStatement ps =
                    conn.prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, gender);
            ps.setString(3, branch);
            ps.setString(4, phone);
            ps.setString(5, email);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTeacher(int id) {

        try {

            Connection conn = database.connection();

            String query =
                    "DELETE FROM teachers WHERE id = ?";

            PreparedStatement ps =
                    conn.prepareStatement(query);

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTeacher(
            int id,
            String name,
            String gender
    ) {

        try {

            Connection conn = database.connection();


            String query =
                    "UPDATE teachers SET name=?, gender=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, name);

            ps.setString(2, gender);

            ps.setInt(3, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}