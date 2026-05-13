package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.GUI.Screens.TeacherAssign;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Group_Gen_seat {

    public static ScrollPane showGroup(
            String groupName,
            ExamConfig config
    ) {
        VBox mainContainer = new VBox(20);

        Button printAll = new Button("Print All");
        printAll.getStyleClass().add("primary-btn");
        mainContainer.getChildren().add(printAll);

        // Bahar declare — lambda aur loop dono use karein
        List<String[]> arrData = new ArrayList<>();

        try {
            DB_Methods db = new DB_Methods();
            arrData = db.fetch_group_tables(groupName);

            if (arrData == null || arrData.isEmpty()) {
                System.out.println("No data: " + groupName);
                return new ScrollPane(new VBox());
            }

            for (String[] row : arrData) {

                String tableName = row[0].trim(); // arr_table_name

                // Teachers fetch
                List<Teacher> teachers = null;
                try {
                    int roomNo = Integer.parseInt(row[1].trim());
                    teachers = TeacherAssign.getRoomTeachers().get(roomNo);
                } catch (Exception ignored) {}

                // Preview WebView banana
                List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
                if (data == null || data.isEmpty()) continue;

                String html = Gen_seat.generateHtml(tableName, data, config, teachers);
                javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
                webView.setPrefWidth(794);
                webView.setPrefHeight(1123);
                webView.setMinWidth(794);  webView.setMinHeight(1123);
                webView.setMaxWidth(794);  webView.setMaxHeight(1123);
                webView.setZoom(0.92);
                webView.getEngine().loadContent(html);

                mainContainer.getChildren().add(new VBox(webView));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Final copy for lambda
        final List<String[]> finalArrData = arrData;

        printAll.setOnAction(e -> {
            String html = generateGroupHtml(groupName, finalArrData, config);
            Gen_seat.openHtmlInBrowser(html);
        });

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private static String generateGroupHtml(
            String groupName,
            List<String[]> arrData,
            ExamConfig config
    ) {
        if (arrData == null || arrData.isEmpty()) return "";

        StringBuilder fullHtml = new StringBuilder();
        fullHtml.append("""
<html>
<head>
<style>
body { margin:0; padding:0; background:#808080; }
</style>
</head>
<body>
""");

        for (String[] row : arrData) {

            String tableName = row[0].trim();

            // Teachers fetch
            List<Teacher> teachers = null;
            try {
                int roomNo = Integer.parseInt(row[1].trim());
                teachers = TeacherAssign.getRoomTeachers().get(roomNo);
            } catch (Exception ignored) {}

            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
            if (data == null || data.isEmpty()) continue;

            String html = Gen_seat.generateHtml(tableName, data, config, teachers);
            fullHtml.append(html);
            fullHtml.append("<div style='page-break-after:always'></div>\n");
        }

        fullHtml.append("</body></html>");
        return fullHtml.toString();
    }
}