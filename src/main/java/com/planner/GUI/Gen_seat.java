package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;

public class Gen_seat {

    public static ScrollPane showTablesScreen(
            List<String> tableNames,
            String date,
            String session
    ) {

        VBox main = new VBox(30);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: #F8F9FA;");

        for (String tableName : tableNames) {

            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);

            if (data == null || data.isEmpty()) continue;

            int roomNo = Integer.parseInt(
                    tableName.substring(tableName.lastIndexOf("_") + 1)
            );

            List<Teacher> teachers = Screens.roomTeachers.get(roomNo);

            VBox sheet = createExamSheet(
                    tableName,
                    data,
                    date,
                    session,
                    teachers
            );

            main.getChildren().add(sheet);
        }

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private static VBox createExamSheet(String tableName,
                                        List<List<String>> data,
                                        String date,
                                        String session,
                                        List<Teacher> teachers) {

        VBox sheet = new VBox(10);
        sheet.setPadding(new Insets(20));
        sheet.setStyle("-fx-background-color: white; -fx-border-color: black;");

        String roomNo = tableName.substring(tableName.lastIndexOf("_") + 1);


        Label college = new Label("SAGAR INSTITUTE OF SCIENCE, TECHNOLOGY & RESEARCH BHOPAL (SISTec-R)");
        college.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label exam = new Label("B. Tech. " + session + " EXAMINATION (Seating Plan)");
        exam.setStyle("-fx-font-size: 12px;");

        Label room = new Label("Room No: " + roomNo);
        Label dateLabel = new Label("Date: " + date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox roomRow = new HBox(room, spacer, dateLabel);

        VBox header = new VBox(5, college, exam, roomRow);
        header.setAlignment(Pos.CENTER);


        String branch1 = "u";
        String branch2 = "tt";

        outer:
        for (List<String> row : data) {
            for (String cell : row) {
                if (cell != null && cell.length() >= 6) {
                    String b = cell.substring(4, 6);

                    if (branch1.isEmpty()) {
                        branch1 = b;
                    } else if (!b.equals(branch1)) {
                        branch2 = b;
                        break outer;
                    }
                }
            }
        }

        Label branch = new Label("Branch: " + branch1 + "  " + branch2);
        Label subject = new Label("Subject: Auto Generated"); // later connect DB

        VBox infoBox = new VBox(5, branch, subject);


        Label time = new Label("Time: 10:00 AM to 01:00 PM");


        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        int rows = data.size();
        int cols = data.get(0).size();

        int totalStudents = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                String value = data.get(i).get(j);

                Label cell = new Label(value);
                cell.setMinSize(100, 30);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-border-color: black;");

                if (value != null && !value.equalsIgnoreCase("null")) {
                    totalStudents++;
                }

                grid.add(cell, j, i);
            }
        }


        GridPane summary = new GridPane();
        summary.setHgap(5);
        summary.setVgap(5);
        summary.setStyle("-fx-border-color: black;");

        summary.add(new Label("Paper Code"), 0, 0);
        summary.add(new Label("From"), 1, 0);
        summary.add(new Label("To"), 2, 0);
        summary.add(new Label("Total"), 3, 0);

        summary.add(new Label(branch1), 0, 1);
        summary.add(new Label("----"), 1, 1);
        summary.add(new Label("----"), 2, 1);
        summary.add(new Label(String.valueOf(totalStudents / 2)), 3, 1);

        summary.add(new Label(branch2), 0, 2);
        summary.add(new Label("----"), 1, 2);
        summary.add(new Label("----"), 2, 2);
        summary.add(new Label(String.valueOf(totalStudents / 2)), 3, 2);

        summary.add(new Label("Total"), 2, 3);
        summary.add(new Label(String.valueOf(totalStudents)), 3, 3);

        GridPane invigilator = new GridPane();
        invigilator.setHgap(10);
        invigilator.setVgap(5);

        invigilator.add(new Label("S.No"), 0, 0);
        invigilator.add(new Label("Name of Invigilators"), 1, 0);
        invigilator.add(new Label("Designation"), 2, 0);
        invigilator.add(new Label("Signature"), 3, 0);

// Teacher 1
        invigilator.add(new Label("1"), 0, 1);
        invigilator.add(new Label(
                (teachers != null && teachers.size() > 0)
                        ? teachers.get(0).getName()
                        : ""
        ), 1, 1);

// Teacher 2
        invigilator.add(new Label("2"), 0, 2);
        invigilator.add(new Label(
                (teachers != null && teachers.size() > 1)
                        ? teachers.get(1).getName()
                        : ""
        ), 1, 2);

        sheet.getChildren().addAll(header, infoBox, time, grid, summary, invigilator);

        return sheet;
    }
};