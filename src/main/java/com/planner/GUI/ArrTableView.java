package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.GUI.Screens.DashboardScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;

import java.util.List;

public class ArrTableView {

    public static VBox show(String tableName, String roomNo, String date) {

        VBox mainLayout = new VBox();
        mainLayout.setStyle("-fx-background-color: #F8F9FA;");

        List<List<String>> data = ArrangementsDB.fetcharrData(tableName);

        if (data == null) {
            Notification.message("Sorry! The table " + tableName + " was not found in the database.");
            return mainLayout;
        }
        if (data.isEmpty()) {
            Notification.message("The table was found, but it has no data.");
            return mainLayout;
        }

        String branch1 = "";
        String branch2 = "";
        String branch3 = "";

        for (List<String> row : data) {
            for (String cell : row) {
                if (cell != null && cell.length() >= 6) {
                    String b = cell.substring(4, 6);

                    if (branch1.isEmpty()) {
                        branch1 = b;
                    } else if (!b.equals(branch1) && branch2.isEmpty()) {
                        branch2 = b;
                    } else if (!b.equals(branch1) && !b.equals(branch2) && branch3.isEmpty()) {
                        branch3 = b;
                        break;
                    }
                }
            }
            if (!branch3.isEmpty()) {
                break;
            }
        }

        final String finalBranch1 = branch1;
        final String finalBranch2 = branch2;
        final String finalBranch3 = branch3;
        final List<List<String>> finalData = data;

        VBox headerbox = new VBox(10);
        headerbox.setStyle("-fx-background-color: #F8F9FA;");
        headerbox.setPadding(new Insets(15, 40, 15, 40));

        Label roomLabel = new Label("Room No: " + roomNo);
        roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        HBox roomBox = new HBox(roomLabel);
        roomBox.setAlignment(Pos.CENTER);

        Label dateLabel = new Label("Date: " + date);
        dateLabel.setFont(Font.font("Arial", 13));

        Label branchLabel = new Label("Branch: " + branch1 + "\nBranch: " + branch2 );
        if(branch3!=""){
            branchLabel.setText(branchLabel.getText() + "\nBranch: " + branch3);
        }
        branchLabel.setFont(Font.font("Arial", 13));

        VBox branchdateBox = new VBox(branchLabel, dateLabel);

        Label timelable = new Label("TIME - 10:00 - 01:00");
        timelable.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");

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
        Button tablelablebutton = new Button("Print Table Label");
        tablelablebutton.setStyle(
                "-fx-background-color:#1a56db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );

        VBox headerright = new VBox();
        headerright.setSpacing(10);
        headerright.getChildren().addAll(printBtn,tablelablebutton);

        VBox headerleft = new VBox();
        headerleft.getChildren().addAll(branchdateBox, timelable);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerrowbox = new HBox();
        headerrowbox.getChildren().addAll(headerleft, spacer, headerright);

        headerbox.getChildren().addAll(roomBox, headerrowbox);


        GridPane grid = buildGrid(data);

//Print button action
        printBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                Notification.message("No printer available.");
                return;
            }

            boolean proceed = job.showPrintDialog(null);
            if (!proceed) return;

            PageLayout pageLayout = job.getPrinter().createPageLayout(
                    Paper.A4,
                    PageOrientation.PORTRAIT,
                    javafx.print.Printer.MarginType.DEFAULT
            );
            job.getJobSettings().setPageLayout(pageLayout);

            double pageWidth = pageLayout.getPrintableWidth();
            double pageHeight = pageLayout.getPrintableHeight();

            VBox printContent = buildPrintContent(finalData, finalBranch1, finalBranch2,finalBranch3, roomNo, date);

            Scene tempScene = new Scene(printContent, pageWidth, pageHeight);

            printContent.setStyle("-fx-background-color: white;");
            printContent.applyCss();
            printContent.layout();

            double contentWidth = printContent.getBoundsInLocal().getWidth();
            double contentHeight = printContent.getBoundsInLocal().getHeight();


            double scaleX = pageWidth / contentWidth;
            double scaleY = pageHeight / contentHeight;
            double scale = Math.min(scaleX, scaleY);

            Scale scaleTransform = new Scale(scale, scale, 0, 0);
            printContent.getTransforms().add(scaleTransform);

            boolean success = job.printPage(pageLayout, printContent);
            if (success) {
                job.endJob();
                Notification.message("Printed successfully.");
            } else {
                job.cancelJob();
                Notification.message("Print failed.");
            }
        });


// table lable button action
        tablelablebutton.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                Notification.message("No printer available.");
                return;
            }

            boolean proceed = job.showPrintDialog(null);
            if (!proceed) return;


            PageLayout pageLayout = job.getPrinter().createPageLayout(
                    Paper.A4,
                    PageOrientation.PORTRAIT,
                    javafx.print.Printer.MarginType.DEFAULT
            );
            job.getJobSettings().setPageLayout(pageLayout);

            double pageWidth = pageLayout.getPrintableWidth();
            double pageHeight = pageLayout.getPrintableHeight();

            VBox printContent = buildSlipContent(finalData);

            Scene tempScene = new Scene(printContent, pageWidth, pageHeight);

            printContent.setStyle("-fx-background-color: white;");
            printContent.applyCss();
            printContent.layout();

            double contentWidth = printContent.getBoundsInLocal().getWidth();
            double contentHeight = printContent.getBoundsInLocal().getHeight();

            double scaleX = pageWidth / contentWidth;
            double scaleY = pageHeight / contentHeight;
            double scale = Math.min(scaleX, scaleY);

            Scale scaleTransform = new Scale(scale, scale, 0, 0);
            printContent.getTransforms().add(scaleTransform);

            boolean success = job.printPage(pageLayout, printContent);
            if (success) {
                job.endJob();
                Notification.message("Printed successfully.");
            } else {
                job.cancelJob();
                Notification.message("Print failed.");
            }
        });


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
            HomePage.rightSide.setCenter(DashboardScreen.dashboardContent(new HomePage()));
            HomePage.rightSide.setTop(HomePage.createTopBar("Dashboard"));
        });

        HBox bottombox = new HBox();
        bottombox.setStyle("-fx-background-color: #F8F9FA;");
        bottombox.getChildren().add(backbutton);
        bottombox.setPadding(new Insets(10));
        bottombox.setAlignment(Pos.BOTTOM_LEFT);

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.getChildren().addAll(headerbox, grid, spacer1, bottombox);
        return mainLayout;
    }

//build grid method
    private static GridPane buildGrid(List<List<String>> data) {
        int colCount = data.size();
        int rowCount = data.get(0).size();

        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #F8F9FA;");
        grid.setHgap(2);
        grid.setVgap(2);

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                String cellValue = (c < data.size() && r < data.get(c).size()) ? data.get(c).get(r) : "";

                Label cell = new Label(cellValue);
                cell.setMinWidth(140);
                cell.setMinHeight(40);
                cell.setAlignment(Pos.CENTER);
                cell.setPadding(new Insets(4));
                cell.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                if ((r + c) % 2 == 0) {
                    cell.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                } else {
                    cell.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                }
                grid.add(cell, c, r);
            }
        }
        return grid;
    }

//build print content
    private static VBox buildPrintContent(List<List<String>> data, String branch1, String branch2,String branch3,
                                          String roomNo, String date) {
        Label roomLabel = new Label("Room No: " + roomNo);
        roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        HBox roomBox = new HBox(roomLabel);
        roomBox.setAlignment(Pos.CENTER_LEFT);

        Label branchLabel = new Label("Branch: " + branch1 + "    Branch: " + branch2+ "    Branch: " + branch3);
        branchLabel.setFont(Font.font("Arial", 13));

        Label dateLabel = new Label("Date: " + date);
        dateLabel.setFont(Font.font("Arial", 13));

        Label timeLabel = new Label("TIME - 10:00 - 01:00");
        timeLabel.setFont(Font.font("Arial", 13));

        VBox infoBox = new VBox(4, roomBox, branchLabel, dateLabel, timeLabel);
        infoBox.setPadding(new Insets(0, 0, 10, 0));

        GridPane grid = buildGrid(data);

        VBox printContent = new VBox(10, infoBox, grid);
        printContent.setPadding(new Insets(20));
        printContent.setAlignment(Pos.TOP_LEFT);
        printContent.setStyle("-fx-background-color: white;");
        return printContent;
    }

//build slip Content method
    private static VBox buildSlipContent(List<List<String>> data) {
        VBox page = new VBox(5);
        page.setPadding(new Insets(10));
        page.setStyle("-fx-background-color: white;");

        int colCount = data.size();
        int rowCount = data.get(0).size();

        int labelsPerRow = 3;
        HBox currentRow = null;
        int labelCount = 0;

        for (int c = 0; c < colCount; c++) {
            for (int r = 0; r < rowCount; r++) {
                String rollNo = (c < data.size() && r < data.get(c).size()) ? data.get(c).get(r) : "";
                if (rollNo == null || rollNo.isEmpty()) continue;

                Label rollLabel = new Label(rollNo);
                rollLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                rollLabel.setAlignment(Pos.CENTER);
                rollLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(rollLabel, Priority.ALWAYS);

                HBox slip = new HBox(rollLabel);
                slip.setAlignment(Pos.CENTER);
                slip.setPrefWidth(200);
                slip.setMinWidth(200);
                slip.setMaxWidth(200);

                slip.setPrefHeight(50);
                slip.setMinHeight(50);
                slip.setMaxHeight(50);
                slip.setStyle(
                        "-fx-border-color: black;" +
                                "-fx-border-width: 1;" +
                                "-fx-background-color: white;"
                );

                if (labelCount % labelsPerRow == 0) {
                    currentRow = new HBox(10);
                    page.getChildren().add(currentRow);
                }

                currentRow.getChildren().add(slip);
                labelCount++;
            }
        }

        return page;
    }
}