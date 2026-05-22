package com.planner.GUI.Screens;

import java.util.HashMap;
import java.util.Map;

import com.planner.Arrangement.Arrange;
import com.planner.Arrangement.ArrangeV2;
import com.planner.GUI.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import com.planner.Database.DB_Methods;


public class ConfirmScreen {

    public static ScrollPane show(
            HomePage app,
            ExamConfig config,
            List<Room> selectedRooms,
            String fileName
    ) {
        VBox main = new VBox(20);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: #F8F9FA;");

        Label title = new Label("Confirm Arrangement Details");
        title.getStyleClass().add("title");

        GridPane info = new GridPane();
        info.setVgap(10);
        info.setHgap(20);

//        info.add(new Label("Arrangement Name:"), 0, 0);
//        info.add(new Label(config.getArrangementName()), 1, 0);

//        info.add(new Label("College:"), 0, 1);
//        info.add(new Label(config.getCollegeName()), 1, 1);

        info.add(new Label("Semester:"), 0, 1);
        info.add(new Label(config.getSemester()), 1, 1);

        info.add(new Label("Session:"), 0, 2);
        info.add(new Label(config.getSession()), 1, 2);

        info.add(new Label("Date:"), 0, 3);
        info.add(new Label(DateUtil.formatForUI(config.getDate())), 1, 3);

        info.add(new Label("Time:"), 0, 4);
        info.add(new Label(config.getExamTime()), 1, 4);

//        info.add(new Label("Subject:"), 0, 5);
//        info.add(new Label(config.getSubject()), 1, 5);

        info.add(new Label("Excel File:"), 0, 6);
        info.add(new Label(fileName), 1, 6);

        Label infoTitle =
                new Label("Arrangement Information");

        infoTitle.setStyle(
                "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        VBox infoCard = new VBox(
                15,
                infoTitle,
                info
        );
        infoCard.getStyleClass().add("card");

        VBox roomBox = new VBox(14);

        Map<Integer, TextField> maleTeacherFields =
                new HashMap<>();

        Map<Integer, TextField> femaleTeacherFields =
                new HashMap<>();

        Label roomTitle =
                new Label("Selected Examination Rooms");

        roomTitle.setStyle(
                "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        Button assignBtn =
                new Button("Auto Assign Teachers");

        assignBtn.getStyleClass().add("primary-btn");

        assignBtn.setOnAction(e -> {

            boolean assigned =
                    TeacherAssign.autoAssignTeachers(
                            selectedRooms,
                            DateUtil.formatForDB(config.getDate()),
                            config.getSession()
                    );

            if (assigned) {

                Map<Integer, List<Teacher>> data =
                        TeacherAssign.getRoomTeachers();

                for (Integer roomNo : data.keySet()) {

                    List<Teacher> teachers =
                            data.get(roomNo);

                    if (teachers.size() >= 2) {

                        maleTeacherFields
                                .get(roomNo)
                                .setText(
                                        teachers.get(0).getName()
                                );

                        femaleTeacherFields
                                .get(roomNo)
                                .setText(
                                        teachers.get(1).getName()
                                );
                    }
                }

                Notification.message(
                        "Teachers assigned successfully!"
                );
            }
        });

        HBox roomHeader = new HBox(
                roomTitle,
                assignBtn
        );

        roomHeader.setAlignment(Pos.CENTER);

        HBox.setHgrow(roomTitle, javafx.scene.layout.Priority.ALWAYS);

        VBox roomCard = new VBox(
                15,
                roomHeader,
                roomBox
        );
        roomCard.setPadding(new Insets(20));
        roomCard.getStyleClass().add("card");

        int totalCapacity = 0;

        for (Room room : selectedRooms) {

            VBox roomItem = new VBox(8);

            roomItem.setPadding(new Insets(16));

            roomItem.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: #E5E7EB;" +
                            "-fx-border-radius: 16;"
            );

            roomItem.setOnMouseEntered(e -> {

                roomItem.setStyle(
                        "-fx-background-color: #FAFAFA;" +
                                "-fx-background-radius: 16;" +
                                "-fx-border-color: #CBD5E1;" +
                                "-fx-border-radius: 16;"
                );
            });

            roomItem.setOnMouseExited(e -> {

                roomItem.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 16;" +
                                "-fx-border-color: #E5E7EB;" +
                                "-fx-border-radius: 16;"
                );
            });

            Label roomName =
                    new Label("Room " + room.getRoomNo());

            roomName.setStyle(
                    "-fx-font-size: 15;" +
                            "-fx-font-weight: bold;"
            );

            Label roomInfo =
                    new Label(
                            "Rows: " + room.getRows() +
                                    " • Columns: " + room.getColumns() +
                                    " • Capacity: " + room.getCapacity()
                    );

            roomInfo.setStyle(
                    "-fx-text-fill: #6B7280;" +
                            "-fx-font-size: 12;"
            );

            Label maleLabel =
                    new Label("Male Invigilator");

            maleLabel.setStyle(
                    "-fx-font-weight: bold;"
            );

            TextField maleField = new TextField();
            maleField.setStyle(
                    "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-padding: 10;" +
                            "-fx-border-color: #D1D5DB;"
            );
            maleField.setPromptText(
                    "Enter male teacher name"
            );

            Label femaleLabel =
                    new Label("Female Invigilator");

            femaleLabel.setStyle(
                    "-fx-font-weight: bold;"
            );

            TextField femaleField = new TextField();
            femaleField.setStyle(
                    "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-padding: 10;" +
                            "-fx-border-color: #D1D5DB;"
            );

            femaleField.setPromptText(
                    "Enter female teacher name"
            );

            maleTeacherFields.put(
                    room.getRoomNo(),
                    maleField
            );

            femaleTeacherFields.put(
                    room.getRoomNo(),
                    femaleField
            );

            roomItem.getChildren().addAll(
                    roomName,
                    roomInfo,
                    maleLabel,
                    maleField,
                    femaleLabel,
                    femaleField
            );

            roomBox.getChildren().add(roomItem);

            totalCapacity += room.getCapacity();
        }

        Label totalCap = new Label("Total Capacity: " + totalCapacity);
        totalCap.setStyle(
                "-fx-background-color: #EEF2FF;" +
                        "-fx-text-fill: #3730A3;" +
                        "-fx-padding: 8 14;" +
                        "-fx-background-radius: 30;" +
                        "-fx-font-weight: bold;"
        );

        roomBox.getChildren().add(totalCap);


        Label teacherHint =
                new Label(
                        "You can manually edit assigned teacher names before generation."
                );

        teacherHint.setStyle(
                "-fx-text-fill: #6B7280;" +
                        "-fx-font-size: 11;"
        );

        roomBox.getChildren().add(teacherHint);

        Label students = new Label(
                "Total Students: " + SharedData.totalStudentsLabel.getText()
        );
        students.setStyle(
                "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Button confirmBtn = new Button("Generate Seating Arrangement");
        confirmBtn.getStyleClass().add("primary-btn");

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("primary-btn");

        confirmBtn.setOnAction(e -> {

            // Fetch busy teachers from DB for the current date & session
            String dbDate = DateUtil.formatForDB(config.getDate());
            String session = config.getSession();
            Set<String> busyTeachers = DB_Methods.fetchBusyTeachers(dbDate, session);
            Set<String> busyTeachersLower = new HashSet<>();
            for (String bt : busyTeachers) {
                busyTeachersLower.add(bt.toLowerCase().trim());
            }

            // Validate self-conflicts and database conflicts
            Map<String, String> currentScreenTeachers = new HashMap<>(); // nameLower -> description of room

            for (Room room : selectedRooms) {
                String maleName = maleTeacherFields.get(room.getRoomNo()).getText().trim();
                String femaleName = femaleTeacherFields.get(room.getRoomNo()).getText().trim();

                // Validate male teacher
                if (!maleName.isEmpty() && !maleName.equalsIgnoreCase("null")) {
                    String maleLower = maleName.toLowerCase();
                    if (currentScreenTeachers.containsKey(maleLower)) {
                        Notification.message("Conflict: Teacher '" + maleName + "' is assigned to multiple rooms: " +
                                currentScreenTeachers.get(maleLower) + " and Room " + room.getRoomNo());
                        return;
                    }
                    if (busyTeachersLower.contains(maleLower)) {
                        Notification.message("Conflict: Teacher '" + maleName + "' is already assigned to a duty in another arrangement on " +
                                DateUtil.formatForUI(config.getDate()) + " (" + session + ")");
                        return;
                    }
                    currentScreenTeachers.put(maleLower, "Room " + room.getRoomNo());
                }

                // Validate female teacher
                if (!femaleName.isEmpty() && !femaleName.equalsIgnoreCase("null")) {
                    String femaleLower = femaleName.toLowerCase();
                    if (currentScreenTeachers.containsKey(femaleLower)) {
                        Notification.message("Conflict: Teacher '" + femaleName + "' is assigned to multiple rooms: " +
                                currentScreenTeachers.get(femaleLower) + " and Room " + room.getRoomNo());
                        return;
                    }
                    if (busyTeachersLower.contains(femaleLower)) {
                        Notification.message("Conflict: Teacher '" + femaleName + "' is already assigned to a duty in another arrangement on " +
                                DateUtil.formatForUI(config.getDate()) + " (" + session + ")");
                        return;
                    }
                    currentScreenTeachers.put(femaleLower, "Room " + room.getRoomNo());
                }
            }

            System.out.println("=== DEBUG ===");
            System.out.println("Date: " + config.getDate());
            System.out.println("Session: " + config.getSession());
            System.out.println("Semester: " + config.getSemester());
            System.out.println("ExamTime: " + config.getExamTime());
            System.out.println("=============");

            // Arrange arrange = new Arrange();
            ArrangeV2 arrangeV2 = new ArrangeV2();

            int[] roomsArray = selectedRooms.stream()
                    .mapToInt(Room::getRoomNo)
                    .toArray();

            TeacherAssign.getRoomTeachers().clear();

            for (Room room : selectedRooms) {

                String maleName = maleTeacherFields
                        .get(room.getRoomNo())
                        .getText()
                        .trim();

                String femaleName = femaleTeacherFields
                        .get(room.getRoomNo())
                        .getText()
                        .trim();

                List<Teacher> list = new ArrayList<>();

                if (!maleName.isEmpty()) {
                    list.add(new Teacher(maleName, "Male"));
                }

                if (!femaleName.isEmpty()) {
                    list.add(new Teacher(femaleName, "Female"));
                }

                // ← Yahi fix hai — empty list ho toh bhi room add karo
                TeacherAssign.getRoomTeachers()
                        .put(room.getRoomNo(), list);
            }

            try {
                String grpname = arrangeV2.arrange(
                        roomsArray,
                        DateUtil.formatForDB(config.getDate()),
                        config.getArrangementName(),
                        config.getSemester(),
                        config.getSession(),
                        "SISTecR"
                );

                app.switchCenter(
                        Group_Gen_seat.showGroup(grpname, config)
                );

                Notification.message("Seating generated successfully!");

            } catch (Exception ex) {
                System.out.println(ex.toString());
                ex.printStackTrace();
                Notification.message("Error generating seating");
            }
        });

        HBox buttons = new HBox(10, backBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        main.getChildren().addAll(
                title,
                infoCard,
                roomCard,
                students,
                buttons
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        return scrollPane;
    }


}
