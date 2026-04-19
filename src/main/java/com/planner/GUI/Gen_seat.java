
package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Gen_seat {

        public static void showtable(String tableName) {

            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);


            if (data == null) {
                Notification.message("Kshama karein! Table '" + tableName + "' database mein nahi mili.\nKripya pehle arrangement generate karein.");
                return;
            }
            if (data.isEmpty()) {
                Notification.message("Table toh mil gayi par usme koi data nahi hai.");
                return;
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
            mainLayout.setPadding(new Insets(20));
            mainLayout.setStyle("-fx-background-color: white;");


            Label branchLabel = new Label("Branch: " + branch1 + "\nBranch: " + branch2);
            branchLabel.setFont(Font.font("Arial", 13));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox branchdateBox = new HBox(branchLabel, spacer);

            Label timelable = new Label("TIME - 10:00 - 01:00");
            timelable.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");

            // --- Grid Setup ---
            int colCount = data.size();
            int rowCount = data.get(0).size();

            GridPane grid = new GridPane();
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

            // --- Buttons & Actions ---
            Button printBtn = new Button("Print");

            printBtn.setOnAction(e -> {
                javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
                if (job != null && job.showPrintDialog(null)) {
                    if (job.printPage(mainLayout)) job.endJob();
                }
            });




            HBox btnBox = new HBox(10, printBtn);
            btnBox.setAlignment(Pos.CENTER_RIGHT);

            mainLayout.getChildren().addAll(branchdateBox, timelable, grid, btnBox);

            // --- Window Show ---
            Stage stage = new Stage();
            stage.setTitle("Exam Seating Arrangement");
            stage.setScene(new Scene(mainLayout, 750, 500));
            stage.show();
        }

    }

