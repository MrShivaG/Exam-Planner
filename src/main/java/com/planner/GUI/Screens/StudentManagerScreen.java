package com.planner.GUI.Screens;

import com.planner.GUI.HomePage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentManagerScreen {

    public static Node showStudents(

            HomePage app,
            List<String> students

    ) {

        VBox root =
                new VBox(18);

        root.setPadding(
                new Insets(25)
        );

        root.setStyle(
                "-fx-background-color:#F5F7FA;"
        );

        Label heading =
                new Label(
                        "Student Management"
                );

        heading.setStyle("""
                -fx-font-size:24px;
                -fx-font-weight:800;
                -fx-text-fill:#111827;
        """);

        TextField search =
                new TextField();

        search.setPromptText(
                "Search enrollment no..."
        );

        search.setStyle("""
                -fx-background-radius:12;
                -fx-padding:12;
                -fx-font-size:14px;
                -fx-background-color:white;
                -fx-border-color:#E5E7EB;
                -fx-border-radius:12;
        """);

        Label total =
                new Label(
                        "Total Students : "
                                + students.size()
                );

        total.setStyle("""
                -fx-font-size:15px;
                -fx-font-weight:700;
                -fx-text-fill:#374151;
        """);

        VBox studentList =
                new VBox(12);

        for (String enroll : students) {

            HBox row =
                    createStudentCard(
                            enroll
                    );

            studentList
                    .getChildren()
                    .add(row);
        }

        ScrollPane scrollPane =
                new ScrollPane(studentList);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle("""
                -fx-background:transparent;
                -fx-background-color:transparent;
        """);

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        root.getChildren().addAll(
                heading,
                search,
                total,
                scrollPane
        );

        return root;
    }

    private static HBox createStudentCard(
            String enroll
    ) {

        HBox row =
                new HBox(14);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(14)
        );

        row.setStyle("""
                -fx-background-color:white;
                -fx-background-radius:16;
                -fx-border-color:#E5E7EB;
                -fx-border-radius:16;
        """);

        Label enrollLabel =
                new Label(enroll);

        enrollLabel.setStyle("""
                -fx-font-size:16px;
                -fx-font-weight:800;
                -fx-text-fill:#111827;
        """);

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button addBtn =
                new Button("+");

        addBtn.setStyle("""
                -fx-background-color:#DCFCE7;
                -fx-text-fill:#166534;
                -fx-font-size:16px;
                -fx-font-weight:bold;
                -fx-background-radius:100;
                -fx-pref-width:38;
                -fx-pref-height:38;
                -fx-cursor:hand;
        """);

        Button deleteBtn =
                new Button("🗑");

        deleteBtn.setStyle("""
                -fx-background-color:#FEE2E2;
                -fx-text-fill:#991B1B;
                -fx-font-size:15px;
                -fx-font-weight:bold;
                -fx-background-radius:100;
                -fx-pref-width:38;
                -fx-pref-height:38;
                -fx-cursor:hand;
        """);

        row.getChildren().addAll(
                enrollLabel,
                spacer,
                addBtn,
                deleteBtn
        );

        return row;
    }

//
//    List<String> students =
//            List.of(
//                    "220111001",
//                    "220111002",
//                    "220111003"
//            );
//
//app.switchScreen(
//        HomePage.createTopBar(
//        "Students"
//        ),
//        StudentManagerScreen.showStudents(
//    app,
//    students
//        )
//                );
}