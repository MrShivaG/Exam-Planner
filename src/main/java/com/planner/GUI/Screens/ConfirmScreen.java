package com.planner.GUI.Screens;

import java.util.Map;

import com.planner.Arrangement.Arrange;
import com.planner.GUI.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ConfirmScreen {

    public static VBox show(
            HomePage app,
            ExamConfig config,
            List<Room> selectedRooms,
            String fileName
    ){
        VBox main = new VBox(20);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: #F8F9FA;");

        Label title = new Label("Confirm Arrangement Details");
        title.getStyleClass().add("title");

        GridPane info = new GridPane();
        info.setVgap(10);
        info.setHgap(20);

        info.add(new Label("Arrangement Name:"), 0, 0);
        info.add(new Label(config.getArrangementName()), 1, 0);

        info.add(new Label("College:"), 0, 1);
        info.add(new Label(config.getCollegeName()), 1, 1);

        info.add(new Label("Session:"), 0, 2);
        info.add(new Label(config.getSession()), 1, 2);

        info.add(new Label("Date:"), 0, 3);
        info.add(new Label(DateUtil.formatForUI(config.getDate())), 1, 3);

        info.add(new Label("Time:"), 0, 4);
        info.add(new Label(config.getExamTime()), 1, 4);

        info.add(new Label("Subject:"), 0, 5);
        info.add(new Label(config.getSubject()), 1, 5);

        info.add(new Label("Excel File:"), 0, 6);
        info.add(new Label(fileName), 1, 6);

        VBox infoCard = new VBox(info);
        infoCard.getStyleClass().add("card");

        VBox roomBox = new VBox(10);

        int totalCapacity = 0;

        for (Room room : selectedRooms) {
            Label r = new Label(
                    "Room " + room.getRoomNo() +
                            " | Rows: " + room.getRows() +
                            " | Columns: " + room.getColumns() +
                            " | Capacity: " + room.getCapacity()
            );
            roomBox.getChildren().add(r);
            totalCapacity += room.getCapacity();
        }

        Label totalCap = new Label("Total Capacity: " + totalCapacity);

        roomBox.getChildren().add(totalCap);

        VBox roomCard = new VBox(roomBox);
        roomCard.getStyleClass().add("card");

        VBox teacherBox = new VBox(10);

        Button assignBtn =
                new Button("Auto Assign Teachers");

        assignBtn.getStyleClass().add("primary-btn");

        VBox assignedTeachersBox =
                new VBox(10);

        assignBtn.setOnAction(e -> {

            boolean assigned =
                    TeacherAssign.autoAssignTeachers(
                            selectedRooms
                    );

            if (assigned) {

                assignedTeachersBox.getChildren().clear();

                Map<Integer, List<Teacher>> data =
                        TeacherAssign.getRoomTeachers();

                for (Integer roomNo : data.keySet()) {

                    VBox card = new VBox(5);

                    card.setPadding(new Insets(12));

                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-color: #E5E7EB;" +
                                    "-fx-border-radius: 12;"
                    );

                    Label roomTitle =
                            new Label("Room " + roomNo);

                    roomTitle.setStyle(
                            "-fx-font-size: 14;" +
                                    "-fx-font-weight: bold;"
                    );

                    VBox teachersList =
                            new VBox(4);

                    for (Teacher t : data.get(roomNo)) {

                        Label teacherLabel =
                                new Label(
                                        "• " + t.getName()
                                );

                        teachersList
                                .getChildren()
                                .add(teacherLabel);
                    }

                    card.getChildren().addAll(
                            roomTitle,
                            teachersList
                    );

                    assignedTeachersBox
                            .getChildren()
                            .add(card);
                }

                Notification.message(
                        "Teachers assigned successfully!"
                );
            }
        });

        VBox teacherCard = new VBox(
                15,
                assignBtn,
                assignedTeachersBox
        );

        teacherCard.setPadding(new Insets(20));

        teacherCard.getStyleClass().add("card");

        Label students = new Label(
                "Total Students: " + SharedData.totalStudentsLabel.getText()
        );

        Button confirmBtn = new Button("Confirm & Generate");
        confirmBtn.getStyleClass().add("primary-btn");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("primary-btn");

        confirmBtn.setOnAction(e -> {

            Arrange arrange = new Arrange();

            int[] roomsArray = selectedRooms.stream()
                    .mapToInt(Room::getRoomNo)
                    .toArray();

            try {
                ArrayList<String> tables = arrange.arrange(
                        roomsArray,
                        DateUtil.formatForDB(config.getDate()),
                        config.getSession()
                );
//90000
                app.switchCenter(
                        Gen_seat.showTablesScreen(tables, config)
                );

                System.out.println("Seating Generated Successfully!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Seating Generated Successfully!");
                alert.showAndWait();

            } catch (Exception ex) {
                Notification.message("Error generating seating");
            }
        });

        backBtn.setOnAction(e -> {
            app.switchCenter(RoomTableScreen.roomTableScreen(app, config));
        });

        HBox buttons = new HBox(10, backBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        main.getChildren().addAll(
                title,
                infoCard,
                roomCard,
                teacherCard,
                students,
                buttons
        );

        return main;
    }


}
