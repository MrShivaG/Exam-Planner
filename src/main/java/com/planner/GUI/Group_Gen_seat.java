package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.GUI.Screens.TeacherAssign;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.planner.GUI.Panel.enrollmentListPanel;


public class Group_Gen_seat {

    public static BorderPane showGroup(
            String groupName,
            ExamConfig config
    ) {
        // LEFT — preview
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));

        Button printAll = new Button("Print All");
        printAll.getStyleClass().add("primary-btn");
        mainContainer.getChildren().add(printAll);

        List<String[]> arrData = new ArrayList<>();
        List<String> detectedPaperCodes = new ArrayList<>();

        List<String[]> enrollments = new ArrayList<>();

        List<String> subjectCodes =
                new ArrayList<>();

        try {
            DB_Methods db = new DB_Methods();
            arrData = db.fetch_group_tables(groupName);

            if (arrData == null || arrData.isEmpty()) {
                return new BorderPane(new ScrollPane(new VBox()));
            }

            for (String[] row : arrData) {
                String tableName = row[0].trim();

                subjectCodes.add(tableName);

                String roomNO = row[1].trim();
                String rangeTableName = row[6] != null ? row[6].trim() : "";
                int rowsRoom = 6;
                try {
                    rowsRoom = Integer.parseInt(row[7].trim());
                } catch (Exception ignored) {
                }

                List<Teacher> teachers = null;
                try {
                    boolean hasDbTeachers = false;
                    List<Teacher> dbTeachers = new ArrayList<>();
                    if (row.length > 8 && row[8] != null && !row[8].trim().isEmpty() && !row[8].equalsIgnoreCase("null")) {
                        dbTeachers.add(new Teacher(row[8].trim(), "Male"));
                        hasDbTeachers = true;
                    }
                    if (row.length > 9 && row[9] != null && !row[9].trim().isEmpty() && !row[9].equalsIgnoreCase("null")) {
                        dbTeachers.add(new Teacher(row[9].trim(), "Female"));
                        hasDbTeachers = true;
                    }
                    if (hasDbTeachers) {
                        teachers = dbTeachers;
                    }
                } catch (Exception ignored) {
                }

                List<List<String>> data = ArrangementsDB.fetcharrData(tableName);

// PEHLE null check
                if (data == null || data.isEmpty()) continue;

// PHIR enrollments fill karo
                for (List<String> dataRow : data) {
                    if (dataRow.size() > 1 &&
                            dataRow.get(1) != null &&
                            !dataRow.get(1).equalsIgnoreCase("null") &&
                            !dataRow.get(1).isEmpty()) {
                        enrollments.add(new String[]{
                                dataRow.get(1),
                                tableName
                        });
                    }
                }

                List<String> enrolls = data.stream()
                        .map(r -> r.size() > 1 ? r.get(1) : "")
                        .collect(Collectors.toList());

                List<List<String>> rangeRaw = new ArrayList<>();
                if (!rangeTableName.isEmpty()) {
                    List<List<String>> fetched = ArrangementsDB.fetcharrData(rangeTableName);
                    if (fetched != null) {
                        rangeRaw = fetched;
                        // PaperCodes collect karo
                        for (List<String> rr : fetched) {
                            if (rr.size() > 1 && rr.get(1) != null
                                    && !rr.get(1).equalsIgnoreCase("null")
                                    && !detectedPaperCodes.contains(rr.get(1))) {
                                detectedPaperCodes.add(rr.get(1));
                            }
                        }
                    }
                }

                String html = Gen_seat.generateHtml(
                        roomNO, tableName, enrolls, config, teachers, rowsRoom, rangeRaw
                );

                javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
                webView.setPrefWidth(794);
                webView.setPrefHeight(1123);
                webView.setMinWidth(794);
                webView.setMinHeight(1123);
                webView.setMaxWidth(794);
                webView.setMaxHeight(1123);
                webView.setZoom(0.92);
                webView.getEngine().loadContent(html);
                mainContainer.getChildren().add(new VBox(webView));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<String[]> finalArrData = arrData;
        printAll.setOnAction(e -> {
            String html = generateGroupHtml(groupName, finalArrData, config);
            Gen_seat.openHtmlInBrowser(html);
        });

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);

        BorderPane layout = new BorderPane();
        layout.setCenter(scrollPane);


        // Right panel container
        VBox rightPanelContainer = new VBox();
        rightPanelContainer.setPrefWidth(340);
        rightPanelContainer.setMinWidth(340);
        rightPanelContainer.setMaxWidth(340);
        rightPanelContainer.setStyle("""
    -fx-background-color: #F8FAFC;
    -fx-border-color: #E5E7EB;
    -fx-border-width: 0 0 0 1;
    """);

// Height bind karo scene se
        javafx.application.Platform.runLater(() -> {
            if (rightPanelContainer.getScene() != null) {
                rightPanelContainer.prefHeightProperty().bind(
                        rightPanelContainer.getScene().heightProperty()
                );
            }
        });

        System.out.println("Enrollments before panel: " + enrollments.size());
        VBox firstPanel = enrollmentListPanel(
                enrollments, rightPanelContainer, detectedPaperCodes, finalArrData
        );
        System.out.println("firstPanel children: " + firstPanel.getChildren().size());
        VBox.setVgrow(firstPanel, Priority.ALWAYS);
        firstPanel.setMaxHeight(Double.MAX_VALUE);
        rightPanelContainer.getChildren().add(firstPanel);

        layout.setRight(rightPanelContainer);

        layout.heightProperty().addListener((obs, oldVal, newVal) -> {
            rightPanelContainer.setPrefHeight(newVal.doubleValue());
            rightPanelContainer.setMinHeight(newVal.doubleValue());
            // firstPanel bhi update karo
            if (!rightPanelContainer.getChildren().isEmpty()) {
                rightPanelContainer.getChildren().get(0).prefHeight(newVal.doubleValue());
            }
        });
        return layout;
    }

    private static String generateGroupHtml(
            String groupName,
            List<String[]> arrData,
            ExamConfig config
    ) {
        if (arrData == null || arrData.isEmpty()) return "";

        StringBuilder pagesHtml = new StringBuilder();

        for (String[] row : arrData) {

            String tableName      = row[0].trim();
            String roomNo         = row[1].trim();
            String rangeTableName = row[6] != null ? row[6].trim() : "";
            int rowsRoom = 6;
            try { rowsRoom = Integer.parseInt(row[7].trim()); } catch (Exception ignored) {}

            // Teachers fetch
            List<Teacher> teachers = null;
            try {
                boolean hasDbTeachers = false;
                List<Teacher> dbTeachers = new ArrayList<>();
                if (row.length > 8 && row[8] != null && !row[8].trim().isEmpty() && !row[8].equalsIgnoreCase("null")) {
                    dbTeachers.add(new Teacher(row[8].trim(), "Male"));
                    hasDbTeachers = true;
                }
                if (row.length > 9 && row[9] != null && !row[9].trim().isEmpty() && !row[9].equalsIgnoreCase("null")) {
                    dbTeachers.add(new Teacher(row[9].trim(), "Female"));
                    hasDbTeachers = true;
                }
                if (hasDbTeachers) {
                    teachers = dbTeachers;
                }
            } catch (Exception ignored) {}

            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
            if (data == null || data.isEmpty()) continue;

            // Sirf Enroll_no
            List<String> enrolls = data.stream()
                    .map(r -> r.size() > 1 ? r.get(1) : "")
                    .collect(Collectors.toList());

            // Range table
            List<List<String>> rangeRaw = new ArrayList<>();
            if (!rangeTableName.isEmpty()) {
                List<List<String>> fetched = ArrangementsDB.fetcharrData(rangeTableName);
                if (fetched != null) rangeRaw = fetched;
            }

            String pageHtml = Gen_seat.generateRoomPage(roomNo, tableName, enrolls, config, teachers, rowsRoom, rangeRaw);
            pagesHtml.append(pageHtml);
        }

        return Gen_seat.wrapInHtmlDoc(pagesHtml.toString());
    }

}