package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class ArrTableView {

    public static BorderPane show(String tableName, String roomNo, String date) {

        List<List<String>> data = ArrangementsDB.fetcharrData(tableName);


        if (data == null) {
            Notification.message("Sorry! The table " + tableName + " was not found in the database.");

        }
        if (data.isEmpty()) {
            Notification.message("The table was found, but it has no data.");

        }

        String branch1 = "";
        String branch2 = "";
        outer:
        for (List<String> row : data) {
            for (String cell : row) {
                if (cell != null && cell.length() >= 6) {
                    String b = cell.substring(4, 6);
                    if (branch1.isEmpty()) {
                        branch1 = b;
                    } else if (!b.equals(branch1) && branch2.isEmpty()) {
                        branch2 = b;
                        break outer;
                    }
                }
            }
        }

        final String finalBranch1 = branch1;
        final String finalBranch2 = branch2;
        final List<List<String>> finalData = data;

        // --- Layout Designing ---

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15,40,15,40));



        Label roomLabel = new Label("Room No: " + roomNo);
        roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        HBox roomBox = new HBox(roomLabel);
        roomBox.setAlignment(Pos.CENTER);

        HBox details = new HBox();
        Label dateLabel = new Label("Date: " + date);
        dateLabel.setFont(Font.font("Arial", 13));

        Label branchLabel = new Label("Branch: " + branch1 + "\nBranch: " + branch2);
        branchLabel.setFont(Font.font("Arial", 13));

        VBox branchdateBox = new VBox(branchLabel, dateLabel);

        Label timelable = new Label("TIME - 10:00 - 01:00");
        timelable.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");


        // --- Grid Setup ---
        int colCount = data.size();
        int rowCount = data.get(0).size();

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: white;");
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(2);
        grid.setVgap(2);

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                String cellValue = (c < data.size() && r < data.get(c).size()) ? data.get(c).get(r) : "";

                Label cell = new Label(cellValue);
                cell.setMinWidth(130);
                cell.setMinHeight(30);
                cell.setAlignment(Pos.CENTER);
                cell.setPadding(new Insets(4));
                cell.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                if ((r + c) % 2 == 0) {
                    cell.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                } else {
                    cell.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
                }
                grid.add(cell, c, r);
            }
        }

        //Buttons & Actions
        Button backbutton = new Button("Back");
        backbutton.setStyle(
                "-fx-background-color:#1a56db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );
        backbutton.setOnAction(e -> {
            HomePage.rightSide.setCenter(Screens.dashboardContent(new HomePage()));
            HomePage.rightSide.setTop(HomePage.createTopBar("Dashboard"));
        });
        HBox bottombox = new HBox();
        bottombox.getChildren().add(backbutton);
        bottombox.setPadding(new Insets(10));



        Button printBtn = new Button("Print");
        printBtn.setStyle(
                "-fx-background-color:#1a56db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );


        printBtn.setOnAction(e -> {
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                if (job.printPage(mainLayout)) job.endJob();
            }
        });

        VBox rightbox = new VBox();
        rightbox.setSpacing(20);
        rightbox.getChildren().add(printBtn);



        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Region spacer1 = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        details.getChildren().addAll(branchdateBox,spacer,rightbox);
        mainLayout.getChildren().addAll(roomBox,details,timelable);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(mainLayout);
        borderPane.setCenter(grid);
        borderPane.setBottom(bottombox);
        return borderPane;
    }

}