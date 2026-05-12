package com.planner.GUI.Screens;

import com.planner.Database.DB_Methods;
import com.planner.Database.TeacherDB;
import com.planner.GUI.CardComponent;
import com.planner.GUI.HomePage;
import com.planner.GUI.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

import static com.planner.GUI.Screens.UploadScreen.openFileChooser;

public class TeacherScreen {

    public static BorderPane teacherScreen(HomePage app) {

        BorderPane layout = new BorderPane();

        // Upload Card
        StackPane card = CardComponent.createCard(
                "Upload File",
                "Drag & Drop your File",
                "/upload.png",
                () -> openFileChooser(app)
        );

        // Manual Card
        StackPane manual = CardComponent.createCard(
                "Enter Manually",
                "Enter all Teachers one by one",
                "/upload.png",
                () -> app.switchCenter(createManualPane())
        );

        HBox topCards = new HBox(50);
        topCards.setAlignment(Pos.CENTER);
        topCards.getChildren().addAll(card, manual);

        // Teacher List
        VBox teacherContainer = new VBox(15);
        teacherContainer.setPadding(new Insets(20));
        teacherContainer.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("All Teachers");
        title.setStyle("""
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-text-fill: #111827;
                """);

        TextField searchField = new TextField();

        searchField.setPromptText("Search Teacher...");
        searchField.setMaxWidth(400);

        searchField.setStyle("""
        -fx-background-radius: 12;
        -fx-border-radius: 12;
        -fx-padding: 10;
        -fx-border-color: #D1D5DB;
        -fx-font-size: 14px;
        """);

        teacherContainer.getChildren().addAll(
                title,
                searchField
        );

        // FETCH TEACHERS FROM DB
        List<Teacher> teachers = TeacherDB.fetchTeachers();
        System.out.println("Teachers Count = " + teachers.size());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            teacherContainer.getChildren().clear();

            teacherContainer.getChildren().add(title);

            for (Teacher teacher : teachers) {

                if (teacher.getName()
                        .toLowerCase()
                        .contains(newVal.toLowerCase())) {

                    teacherContainer.getChildren()
                            .add(createTeacherCard(teacher, app));
                }
            }
        });

        for (Teacher teacher : teachers) {

            HBox teacherCard = createTeacherCard(
                    teacher,
                    app
            );

            teacherContainer.getChildren().add(teacherCard);
        }

        ScrollPane scrollPane = new ScrollPane(teacherContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setStyle("""
                -fx-background-color: transparent;
                -fx-background: transparent;
                """);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.getChildren().addAll(topCards, scrollPane);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContent.setAlignment(Pos.TOP_CENTER);
        layout.setCenter(mainContent);

        return layout;
    }

    private static HBox createTeacherCard(
            Teacher teacher,
            HomePage app
    ) {

        HBox card = new HBox();

        card.setAlignment(Pos.TOP_LEFT);

        card.setSpacing(20);

        card.setPadding(new Insets(18));

        card.setMaxWidth(900);

        card.setMinHeight(90);

        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 14;
            -fx-background-radius: 14;
            """);

        VBox info = new VBox(5);

        Label name = new Label(teacher.getName());

        name.setStyle("""
        -fx-font-size: 18px;
        -fx-font-weight: bold;
        -fx-text-fill: #111827;
        """);

        Label gender = new Label(teacher.getGender());

        gender.setStyle("""
            -fx-background-color: #EFF6FF;
            -fx-text-fill: #2563EB;
            -fx-padding: 4 10 4 10;
            -fx-background-radius: 20;
            -fx-font-size: 12px;
            -fx-font-weight: bold;
            """);

        Button editBtn = new Button("Edit");
        editBtn.setStyle("""
        -fx-background-color: #2563EB;
        -fx-text-fill: white;
        -fx-background-radius: 10;
        -fx-font-weight: bold;
        """);
        editBtn.setOnAction(e -> {

            TextInputDialog dialog =
                    new TextInputDialog(teacher.getName());

            dialog.setTitle("Edit Teacher");

            dialog.setHeaderText("Update Teacher Name");

            dialog.setContentText("Name:");

            Optional<String> result =
                    dialog.showAndWait();

            result.ifPresent(newName -> {

                TeacherDB.updateTeacher(
                        teacher.getId(),
                        newName,
                        teacher.getGender()
                );

                app.switchCenter(
                        TeacherScreen.teacherScreen(app)
                );
            });
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("""
        -fx-background-color: #EF4444;
        -fx-text-fill: white;
        -fx-background-radius: 10;
        -fx-font-weight: bold;
        """);
        deleteBtn.setOnAction(e -> {

            TeacherDB.deleteTeacher(teacher.getId());

            app.switchCenter(
                    TeacherScreen.teacherScreen(app)
            );
        });

        info.getChildren().addAll(name, gender);

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(10);

        buttons.getChildren().addAll(
                editBtn,
                deleteBtn
        );

        card.getChildren().addAll(
                info,
                spacer,
                buttons
        );

        return card;
    }

    private static Node createManualPane() {

        StackPane pane = new StackPane();

        Label label = new Label("Coming Soon");

        pane.getChildren().add(label);

        return pane;
    }
}