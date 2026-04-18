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

public class ArrTableView {

    public static void show(String tableName, String roomNo, String date) {
        try {
            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);


            String branch1 = "";
            String branch2 = "";
            String branch3 = "";
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

            // Main layout
            VBox mainLayout = new VBox(10);
            mainLayout.setPadding(new Insets(20));
            mainLayout.setStyle("-fx-background-color: white;");


            Label roomLabel = new Label("Room No: " + roomNo);
            roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            roomLabel.setUnderline(true);
            HBox roomBox = new HBox(roomLabel);
            roomBox.setAlignment(Pos.CENTER);

            // Date right side
            Label dateLabel = new Label("Date: " + date);
            dateLabel.setFont(Font.font("Arial", 13));
            HBox branchdateBox = new HBox();


            // Branch info
            Label branchLabel = new Label("Branch: " + branch1 + "\nBranch: " + branch2);
            branchLabel.setFont(Font.font("Arial", 13));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            branchdateBox.getChildren().addAll(branchLabel,spacer,dateLabel);

            Label timelable = new Label("TIME - "+"10:00 - 01:00");
            timelable.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: #6B7280;"
            );


            // Grid table
            int colCount = data.size();
            int rowCount = data.get(0).size();

            GridPane grid = new GridPane();
            grid.setHgap(2);
            grid.setVgap(2);

            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < colCount; c++) {
                    String cellValue = "";
                    if (c < data.size() && r < data.get(c).size()) {
                        cellValue = data.get(c).get(r);
                    }

                    Label cell = new Label(cellValue);
                    cell.setMinWidth(130);
                    cell.setMinHeight(30);
                    cell.setAlignment(Pos.CENTER);
                    cell.setPadding(new Insets(4));
                    cell.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                    boolean isBold = (r + c) % 2 == 0;
                    if (isBold) {
                        cell.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    } else {
                        cell.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
                    }

                    grid.add(cell, c, r);
                }
            }

            // Buttons
            Button printBtn = new Button("Print");
            Button saveBtn = new Button("Save as PDF");

            printBtn.setFont(Font.font("Arial", 13));
            saveBtn.setFont(Font.font("Arial", 13));

            // Print action
            printBtn.setOnAction(e -> {
                PrinterJob job = PrinterJob.createPrinterJob();
                if (job != null && job.showPrintDialog(null)) {
                    boolean success = job.printPage(mainLayout);
                    if (success) job.endJob();
                }
            });

            // Save as PDF action
            saveBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("PDF Save Karo");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File", "*.pdf"));
                fileChooser.setInitialFileName(tableName + ".pdf");
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    savePDF(file, finalData, roomNo, date, finalBranch1, finalBranch2);
                }
            });

            HBox btnBox = new HBox(10, printBtn, saveBtn);
            btnBox.setAlignment(Pos.CENTER_RIGHT);

            mainLayout.getChildren().addAll(roomBox, branchdateBox,timelable, grid, btnBox);

            Stage stage = new Stage();
            stage.setTitle("Seating Arrangement");
            Scene scene = new Scene(mainLayout, 750, 500);
            stage.setScene(scene);
            stage.show();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void savePDF(File file, List<List<String>> data,
                                String roomNo, String date,
                                String branch1, String branch2) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float pageWidth = PDRectangle.A4.getWidth();
            float pageHeight = PDRectangle.A4.getHeight();
            float margin = 30;

            int colCount = data.size();
            int rowCount = data.get(0).size();

            float cellWidth = (pageWidth - 2 * margin) / colCount;
            float cellHeight = 22;
            float tableTop = pageHeight - 100;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // Room No - center
                String roomText = "Room No: " + roomNo;
                float roomTextWidth = fontBold.getStringWidth(roomText) / 1000 * 13;
                cs.beginText();
                cs.setFont(fontBold, 13);
                cs.newLineAtOffset((pageWidth - roomTextWidth) / 2, pageHeight - 40);
                cs.showText(roomText);
                cs.endText();

                // Date - right
                String dateText = "Date: " + date;
                float dateWidth = fontNormal.getStringWidth(dateText) / 1000 * 11;
                cs.beginText();
                cs.setFont(fontNormal, 11);
                cs.newLineAtOffset(pageWidth - margin - dateWidth, pageHeight - 60);
                cs.showText(dateText);
                cs.endText();

                // Branch info
                cs.beginText();
                cs.setFont(fontNormal, 11);
                cs.newLineAtOffset(margin, pageHeight - 80);
                cs.showText("Branch: " + branch1 + "          Branch: " + branch2);
                cs.endText();

                // Table
                for (int r = 0; r < rowCount; r++) {
                    for (int c = 0; c < colCount; c++) {
                        String cellValue = "";
                        if (c < data.size() && r < data.get(c).size()) {
                            cellValue = data.get(c).get(r);
                        }

                        float x = margin + c * cellWidth;
                        float y = tableTop - r * cellHeight;

                        // Cell border
                        cs.setLineWidth(0.5f);
                        cs.addRect(x, y - cellHeight, cellWidth, cellHeight);
                        cs.stroke();

                        // Cell text
                        boolean isBold = (r + c) % 2 == 0;
                        cs.beginText();
                        cs.setFont(isBold ? fontBold : fontNormal, 9);
                        cs.newLineAtOffset(x + 4, y - cellHeight + 7);
                        cs.showText(cellValue != null ? cellValue : "");
                        cs.endText();
                    }
                }
            }

            doc.save(file);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}