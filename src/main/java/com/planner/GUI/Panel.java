package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.GUI.Screens.UploadScreen;
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


public class Panel {

    public static VBox enrollmentListPanel(
            List<String[]> enrollments,
            VBox rightPanelContainer,
            List<String> detectedPaperCodes,
            List<String[]> finalArrData
    ) {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(container, Priority.ALWAYS);

        Label title = new Label("Enrolled Students");
        title.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        Label subtitle = new Label(enrollments.size() + " students in seating");
        subtitle.setStyle("""
        -fx-font-size: 11px;
        -fx-text-fill: #6B7280;
        """);

        VBox titleBox = new VBox(4, title, subtitle);
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

        VBox listBox = new VBox(8);
        listBox.setFillWidth(true);
        listBox.setPadding(new Insets(4, 4, 4, 4));

        System.out.println("Total enrollments: " + enrollments.size());
        System.out.println("Total listBox children: " + listBox.getChildren().size());

        for (String[] enrollData : enrollments) {

            String enroll = enrollData[0];
            String tableName = enrollData[1];

            HBox row = new HBox(10);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 10;
            """);

            Label enrollLabel = new Label(enroll);
            enrollLabel.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 600;
            -fx-text-fill: #111827;
            """);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle("""
            -fx-background-color: #FEE2E2;
            -fx-text-fill: #991B1B;
            -fx-background-radius: 100;
            -fx-font-weight: bold;
            -fx-cursor: hand;
            -fx-padding: 4 8 4 8;
            """);

            deleteBtn.setOnAction(e -> {
                boolean confirm = Notification.confirm("Remove " + enroll + "?");
                if (confirm) {
                    ArrangementsDB.softDeleteStudent(tableName, enroll);
                    listBox.getChildren().remove(row);
                    enrollments.remove(enrollData);
                    subtitle.setText(enrollments.size() + " students in seating");
                }
            });

            row.getChildren().addAll(enrollLabel, spacer, deleteBtn);
            listBox.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(listBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setMinHeight(200);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("""
    -fx-background-color: transparent;
    -fx-background: transparent;
    -fx-border-color: transparent;
""");
        scroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            listBox.setMinWidth(newVal.getWidth());
        });
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button deleteAllBtn = new Button("🗑 Remove All");
        deleteAllBtn.setStyle("""
        -fx-background-color: #FEE2E2;
        -fx-text-fill: #991B1B;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        Button nextBtn = new Button("Review Subjects →");
        nextBtn.setStyle("""
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        deleteAllBtn.setOnAction(e -> {
            boolean confirm = Notification.confirm("Remove ALL students?");
            if (confirm) {
                for (String[] ed : new ArrayList<>(enrollments)) {
                    ArrangementsDB.softDeleteStudent(ed[1], ed[0]);
                }
                enrollments.clear();
                listBox.getChildren().clear();
                subtitle.setText("0 students in seating");
            }
        });

        nextBtn.setOnAction(e -> {
            rightPanelContainer.getChildren().clear();
            VBox next = subjectMappingPanel(detectedPaperCodes, rightPanelContainer, enrollments, finalArrData);
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        HBox btnRow = new HBox(10, deleteAllBtn, nextBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(titleBox, sep, scroll, btnRow);


        VBox.setVgrow(container, Priority.ALWAYS);
        container.setMaxHeight(Double.MAX_VALUE);
        container.prefWidthProperty().bind(
                rightPanelContainer.widthProperty()
        );

        return container;
    }

    private static VBox subjectMappingPanel(
            List<String> detectedPaperCodes,
            VBox rightPanelContainer,
            List<String[]> enrollments,
            List<String[]> finalArrData
    ) {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(container, Priority.ALWAYS);

        Label title = new Label("Subject Mapping");
        title.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        Label subtitle = new Label("Map paper codes to RGPV subjects");
        subtitle.setStyle("""
        -fx-font-size: 11px;
        -fx-text-fill: #6B7280;
        """);

        VBox titleBox = new VBox(4, title, subtitle);
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

        List<String[]> rgpvSubjects = new ArrayList<>();
        try {
            DB_Methods db = new DB_Methods();
            rgpvSubjects = db.fetchRgpvSubjects();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final List<String[]> subjects = rgpvSubjects;
        Map<String, String> selectedMap = new LinkedHashMap<>();
        VBox mappingRows = new VBox(12);

        for (String paperCode : detectedPaperCodes) {
            selectedMap.put(paperCode, "");

            Label codeLabel = new Label(paperCode);
            codeLabel.setStyle("""
            -fx-background-color: #EFF6FF;
            -fx-text-fill: #1D4ED8;
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-padding: 4 10 4 10;
            -fx-background-radius: 20;
            """);

            TextField searchField = new TextField();
            searchField.setPromptText("Search subject...");
            searchField.setStyle("""
            -fx-font-size: 11px;
            -fx-background-radius: 6;
            -fx-border-color: #CBD5E1;
            -fx-border-radius: 6;
            -fx-padding: 6 10 6 10;
            """);

            javafx.scene.control.ListView<String> listView = new javafx.scene.control.ListView<>();
            listView.setPrefHeight(100);
            listView.setStyle("""
            -fx-font-size: 11px;
            -fx-background-radius: 6;
            -fx-border-color: #CBD5E1;
            -fx-border-radius: 6;
            """);
            listView.setVisible(false);
            listView.setManaged(false);

            Label selectedLabel = new Label("Not selected");
            selectedLabel.setStyle("""
            -fx-font-size: 10px;
            -fx-text-fill: #9CA3AF;
            -fx-font-style: italic;
            """);

            subjects.stream()
                    .map(s -> s[0] + " — " + s[1])
                    .forEach(listView.getItems()::add);

            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                listView.setVisible(true);
                listView.setManaged(true);
                listView.getItems().clear();
                String lower = newVal.toLowerCase();
                subjects.stream()
                        .filter(s -> s[0].toLowerCase().contains(lower)
                                || s[1].toLowerCase().contains(lower))
                        .map(s -> s[0] + " — " + s[1])
                        .forEach(listView.getItems()::add);
            });

            listView.setOnMouseClicked(ev -> {
                String chosen = listView.getSelectionModel().getSelectedItem();
                if (chosen != null) {
                    searchField.setText(chosen);
                    selectedMap.put(paperCode, chosen);
                    selectedLabel.setText("✓ " + chosen);
                    selectedLabel.setStyle("""
                    -fx-font-size: 10px;
                    -fx-text-fill: #16A34A;
                    -fx-font-weight: bold;
                    """);
                    listView.setVisible(false);
                    listView.setManaged(false);
                }
            });

            mappingRows.getChildren().add(
                    new VBox(6, codeLabel, searchField, listView, selectedLabel)
            );
        }

        ScrollPane mappingScroll = new ScrollPane(mappingRows);
        mappingScroll.setFitToWidth(true);
        mappingScroll.setPrefHeight(500);
        mappingScroll.setMaxHeight(Double.MAX_VALUE);
        mappingScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mappingScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mappingScroll.setStyle("""
    -fx-background-color: transparent;
    -fx-background: transparent;
    -fx-border-color: transparent;
""");
        VBox.setVgrow(mappingScroll, Priority.ALWAYS);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("""
        -fx-background-color: #F3F4F6;
        -fx-text-fill: #374151;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        Button nextBtn = new Button("Upload Absent List →");
        nextBtn.setStyle("""
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        backBtn.setOnAction(e -> {
            rightPanelContainer.getChildren().clear();
            VBox next = enrollmentListPanel(enrollments, rightPanelContainer, detectedPaperCodes, finalArrData);
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        nextBtn.setOnAction(e -> {
            rightPanelContainer.getChildren().clear();
            VBox next = absentUploadPanel(rightPanelContainer, detectedPaperCodes, enrollments, finalArrData);
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        HBox btnRow = new HBox(10, backBtn, nextBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(titleBox, sep, mappingScroll, btnRow);
        // container build hone ke baad, return se pehle
        VBox.setVgrow(container, Priority.ALWAYS);
        container.setMaxHeight(Double.MAX_VALUE);
        container.prefWidthProperty().bind(
                rightPanelContainer.widthProperty()
        );

        return container;
    }

    private static VBox absentUploadPanel(
            VBox rightPanelContainer,
            List<String> detectedPaperCodes,
            List<String[]> enrollments,
            List<String[]> finalArrData
    ) {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(container, Priority.ALWAYS);

        Label title = new Label("Upload Absent List");
        title.setStyle("""
    -fx-font-size: 16px;
    -fx-font-weight: 800;
    -fx-text-fill: #111827;
    """);

        Label subtitle = new Label("Excel with enrollment numbers");
        subtitle.setStyle("""
    -fx-font-size: 11px;
    -fx-text-fill: #6B7280;
    """);

        VBox titleBox = new VBox(4, title, subtitle);
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

        final File[] selectedFile = {null};
        final List<String> parsedEnrollments = new ArrayList<>();

        Label statusLabel = new Label("No file selected");
        statusLabel.setStyle("""
    -fx-font-size: 11px;
    -fx-text-fill: #6B7280;
    -fx-font-style: italic;
    """);

        StackPane uploadCard = CardComponent.createCard(
                "Absent List",
                "Upload Excel (.xlsx)",
                "/upload.png",
                () -> {
                    File file = UploadScreen.chooseExcelFile();
                    if (file != null) {
                        selectedFile[0] = file;
                        parsedEnrollments.clear();
                        try {
                            org.apache.poi.ss.usermodel.Workbook wb =
                                    new org.apache.poi.xssf.usermodel.XSSFWorkbook(file);
                            org.apache.poi.ss.usermodel.Sheet sheet = wb.getSheetAt(0);
                            for (org.apache.poi.ss.usermodel.Row r : sheet) {
                                if (r.getRowNum() == 0) continue;
                                org.apache.poi.ss.usermodel.Cell cell = r.getCell(0);
                                if (cell != null) {
                                    String enroll = cell.getStringCellValue().trim();
                                    if (!enroll.isEmpty()) parsedEnrollments.add(enroll);
                                }
                            }
                            wb.close();
                            statusLabel.setText("✓ " + file.getName() +
                                    " (" + parsedEnrollments.size() + " students)");
                            statusLabel.setStyle("""
                    -fx-font-size: 11px;
                    -fx-text-fill: #16A34A;
                    -fx-font-weight: bold;
                    """);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Notification.message("Error reading file!");
                        }
                    }
                }
        );

        uploadCard.setPrefSize(260, 300);
        uploadCard.setMaxSize(260, 300);

        VBox uploadSection = new VBox(10, uploadCard, statusLabel);
        uploadSection.setAlignment(Pos.CENTER);
        VBox.setVgrow(uploadSection, Priority.ALWAYS);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("""
    -fx-background-color: #F3F4F6;
    -fx-text-fill: #374151;
    -fx-font-weight: 700;
    -fx-background-radius: 8;
    -fx-padding: 10 16 10 16;
    -fx-cursor: hand;
    """);

        Button nextBtn = new Button("Preview Additions →");
        nextBtn.setStyle("""
    -fx-background-color: #0056D2;
    -fx-text-fill: white;
    -fx-font-weight: 700;
    -fx-background-radius: 8;
    -fx-padding: 10 16 10 16;
    -fx-cursor: hand;
    """);

        HBox btnRow = new HBox(10, backBtn, nextBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(titleBox, sep, uploadSection, btnRow);

        backBtn.setOnAction(e -> {
            rightPanelContainer.getChildren().clear();
            VBox next = subjectMappingPanel(detectedPaperCodes, rightPanelContainer, enrollments, finalArrData);
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        nextBtn.setOnAction(e -> {
            if (selectedFile[0] == null) {
                Notification.message("Please select a file first!");
                return;
            }
            if (parsedEnrollments.isEmpty()) {
                Notification.message("No enrollments found!");
                return;
            }
            rightPanelContainer.getChildren().clear();
            VBox next = absentPreviewPanel(
                    new ArrayList<>(parsedEnrollments),
                    finalArrData,
                    rightPanelContainer,
                    detectedPaperCodes,
                    enrollments
            );
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        VBox.setVgrow(container, Priority.ALWAYS);
        container.setMaxHeight(Double.MAX_VALUE);
        container.prefWidthProperty().bind(rightPanelContainer.widthProperty());

        return container;
    }

    private static VBox absentPreviewPanel(
            List<String> absentEnrollments,
            List<String[]> arrData,
            VBox rightPanelContainer,
            List<String> detectedPaperCodes,
            List<String[]> enrollments
    ) {
        VBox container = new VBox(16);
        container.setPadding(new Insets(20));
        container.setFillWidth(true);
        container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(container, Priority.ALWAYS);

        Label title = new Label("Absent Students");
        title.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        Label subtitle = new Label(absentEnrollments.size() + " students to place");
        subtitle.setStyle("""
        -fx-font-size: 11px;
        -fx-text-fill: #6B7280;
        """);

        VBox titleBox = new VBox(4, title, subtitle);
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

        // Null seats fetch
        Map<String, List<Integer>> nullSeats = new LinkedHashMap<>();
        for (String[] row : arrData) {
            String tableName = row[0].trim();
            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
            List<Integer> ids = new ArrayList<>();
            if (data != null) {
                for (List<String> dataRow : data) {
                    if (dataRow.size() > 1 &&
                            (dataRow.get(1) == null ||
                                    dataRow.get(1).equalsIgnoreCase("null") ||
                                    dataRow.get(1).isEmpty())) {
                        try { ids.add(Integer.parseInt(dataRow.get(0))); }
                        catch (Exception ignored) {}
                    }
                }
            }
            nullSeats.put(tableName, ids);
        }

        VBox listBox = new VBox(8);

        for (String enroll : new ArrayList<>(absentEnrollments)) {
            HBox row = new HBox(10);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 10;
            """);

            Label enrollLabel = new Label(enroll);
            enrollLabel.setStyle("""
            -fx-font-size: 12px;
            -fx-font-weight: 600;
            -fx-text-fill: #111827;
            """);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button addBtn = new Button("＋");
            addBtn.setStyle("""
            -fx-background-color: #DCFCE7;
            -fx-text-fill: #166534;
            -fx-background-radius: 100;
            -fx-font-weight: bold;
            -fx-cursor: hand;
            -fx-padding: 4 8 4 8;
            """);

            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle("""
            -fx-background-color: #FEE2E2;
            -fx-text-fill: #991B1B;
            -fx-background-radius: 100;
            -fx-font-weight: bold;
            -fx-cursor: hand;
            -fx-padding: 4 8 4 8;
            """);

            addBtn.setOnAction(e -> {
                boolean added = addToNullSeat(nullSeats, enroll);
                if (added) {
                    row.setStyle("""
                    -fx-background-color: #F0FDF4;
                    -fx-background-radius: 10;
                    -fx-border-color: #86EFAC;
                    -fx-border-radius: 10;
                    """);
                    addBtn.setDisable(true);
                    Notification.message(enroll + " added!");
                } else {
                    Notification.message("No empty seats available!");
                }
            });

            deleteBtn.setOnAction(e -> {
                absentEnrollments.remove(enroll);
                listBox.getChildren().remove(row);
                subtitle.setText(absentEnrollments.size() + " students to place");
            });

            row.getChildren().addAll(enrollLabel, spacer, addBtn, deleteBtn);
            listBox.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(listBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(Region.USE_COMPUTED_SIZE);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setMinHeight(200);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("""
    -fx-background-color: transparent;
    -fx-background: transparent;
    -fx-border-color: transparent;
""");
        scroll.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            listBox.setMinWidth(newVal.getWidth());
        });
        VBox.setVgrow(scroll, Priority.ALWAYS);
        Button backBtn = new Button("← Back");
        backBtn.setStyle("""
        -fx-background-color: #F3F4F6;
        -fx-text-fill: #374151;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        Button addAllBtn = new Button("＋ Add All");
        addAllBtn.setStyle("""
        -fx-background-color: #DCFCE7;
        -fx-text-fill: #166534;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        Button finalizeBtn = new Button("Finalize Seating ✓");
        finalizeBtn.setStyle("""
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """);

        backBtn.setOnAction(e -> {
            rightPanelContainer.getChildren().clear();
            VBox next = absentUploadPanel(rightPanelContainer, detectedPaperCodes, enrollments, arrData);
            VBox.setVgrow(next, Priority.ALWAYS);
            next.setMaxHeight(Double.MAX_VALUE);
            rightPanelContainer.getChildren().add(next);
        });

        addAllBtn.setOnAction(e -> {
            int count = 0;
            for (String en : new ArrayList<>(absentEnrollments)) {
                if (addToNullSeat(nullSeats, en)) count++;
            }
            for (javafx.scene.Node node : listBox.getChildren()) {
                if (node instanceof HBox hbox) {
                    hbox.setStyle("""
                    -fx-background-color: #F0FDF4;
                    -fx-background-radius: 10;
                    -fx-border-color: #86EFAC;
                    -fx-border-radius: 10;
                    """);
                    hbox.getChildren().stream()
                            .filter(n -> n instanceof Button && ((Button)n).getText().equals("＋"))
                            .forEach(n -> ((Button)n).setDisable(true));
                }
            }
            Notification.message(count + " students added!");
        });

        finalizeBtn.setOnMouseEntered(e -> finalizeBtn.setStyle("""
        -fx-background-color: #003FA3;
        -fx-text-fill: white;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """));
        finalizeBtn.setOnMouseExited(e -> finalizeBtn.setStyle("""
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 700;
        -fx-background-radius: 8;
        -fx-padding: 10 16 10 16;
        -fx-cursor: hand;
        """));
        finalizeBtn.setOnAction(e ->
                Notification.message("Seating finalized successfully!")
        );

        HBox btnRow = new HBox(10, backBtn, addAllBtn, finalizeBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(titleBox, sep, scroll, btnRow);

        VBox.setVgrow(container, Priority.ALWAYS);
        container.setMaxHeight(Double.MAX_VALUE);
        container.prefWidthProperty().bind(
                rightPanelContainer.widthProperty()
        );

        return container;
    }

    private static boolean addToNullSeat(
            Map<String, List<Integer>> nullSeats,
            String enrollNo
    ) {
        for (Map.Entry<String, List<Integer>> entry : nullSeats.entrySet()) {
            List<Integer> ids = entry.getValue();
            if (!ids.isEmpty()) {
                int seatId = ids.remove(0);
                try (Connection con = ArrangementsDB.connection()) {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE `" + entry.getKey() + "` SET Enroll_no = ? WHERE Id = ?"
                    );
                    ps.setString(1, enrollNo);
                    ps.setInt(2, seatId);
                    ps.executeUpdate();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
