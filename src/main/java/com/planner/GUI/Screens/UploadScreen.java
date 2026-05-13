package com.planner.GUI.Screens;

import com.planner.ExcelM.ExcelWork;
import com.planner.GUI.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.planner.GUI.Screens.RoomTableScreen.roomTableScreen;

public class UploadScreen {

    private static File selectedFile = null;

    public static BorderPane dataScreen(HomePage app) {

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #F9FAFB;");

        // ── MAIN SCROLL WRAPPER ─────────────────────────────
        ScrollPane mainScroll = new ScrollPane();
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("""
        -fx-background-color: #F9FAFB;
        -fx-background: #F9FAFB;
        """);

        VBox pageRoot = new VBox(0);
        pageRoot.setStyle("-fx-background-color: #F9FAFB;");

        // ── PAGE HEADER ─────────────────────────────────────
        VBox pageHeader = new VBox(4);
        pageHeader.setPadding(new Insets(32, 40, 20, 40));

        Label pageTitle = new Label("New Exam Arrangement");
        pageTitle.setStyle("""
        -fx-font-size: 24px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        Label pageSub = new Label("Upload student data and configure exam settings");
        pageSub.setStyle("""
        -fx-font-size: 13px;
        -fx-text-fill: #6B7280;
        """);

        pageHeader.getChildren().addAll(pageTitle, pageSub);

        // ── TWO COLUMN LAYOUT ───────────────────────────────
        HBox twoCol = new HBox(24);
        twoCol.setPadding(new Insets(0, 40, 32, 40));
        twoCol.setAlignment(Pos.TOP_CENTER);

        // ── LEFT — Upload Card ──────────────────────────────
        StackPane card = CardComponent.createCard(
                "Upload File",
                "Drag & Drop your File",
                "/upload.png",
                () -> openFileChooser(app)
        );
        card.setPrefSize(280, 380);
        card.setMaxSize(280, 380);
        card.setMinHeight(Region.USE_COMPUTED_SIZE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setPrefWidth(280);
        card.setMaxWidth(280);


        // ── RIGHT — Form Card ───────────────────────────────
        VBox formCard = new VBox(0);
        formCard.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 16;
        -fx-border-color: #E5E7EB;
        -fx-border-radius: 16;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 3);
        """);
        formCard.setPrefWidth(440);
        formCard.setMaxWidth(440);
        formCard.setMinHeight(380);
        formCard.setMaxWidth(380);

        // Form header
        VBox formHeader = new VBox(4);
        formHeader.setPadding(new Insets(24, 28, 20, 28));
        formHeader.setStyle("""
        -fx-border-color: transparent transparent #F3F4F6 transparent;
        -fx-border-width: 1;
        """);

        Label formTitle = new Label("Exam Configuration");
        formTitle.setStyle("""
        -fx-font-size: 16px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        Label formSub = new Label("Fill in the details for this exam session");
        formSub.setStyle("""
        -fx-font-size: 11px;
        -fx-text-fill: #9CA3AF;
        """);

        formHeader.getChildren().addAll(formTitle, formSub);

        // Form fields
        VBox formFields = new VBox(20);
        formFields.setPadding(new Insets(24, 28, 28, 28));

        // Session row
        ComboBox<String> month = new ComboBox<>();
        month.getItems().addAll("June", "Dec");
        month.setPromptText("Month");
        month.setPrefWidth(160);
        month.setPrefHeight(42);
        month.setStyle(comboStyle());
        month.setStyle(comboStyle() + "-fx-background-color: white;");
        month.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Month" : item);
                setStyle("-fx-text-fill: #111827; -fx-background-color: white;");
            }
        });

        ComboBox<String> year = new ComboBox<>();
        int currentYear1 = java.time.Year.now().getValue() % 100;
        for (int i = currentYear1 - 3; i <= 99; i++) {
            year.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < currentYear1 - 3; i++) {
            year.getItems().add(String.format("%02d", i));
        }
        year.setValue(String.format("%02d", currentYear1));
        year.setPromptText("Year");
        year.setEditable(true);
        year.setPrefWidth(120);
        year.setPrefHeight(42);
        year.setStyle(comboStyle());

        HBox sessionRow = new HBox(10, month, year);
        sessionRow.setAlignment(Pos.CENTER_LEFT);

        // Date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(42);
        datePicker.setMaxWidth(180);
        datePicker.setPrefWidth(180);
        datePicker.setStyle(comboStyle());
        datePicker.setValue(LocalDate.now());
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            @Override public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }
            @Override public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, formatter) : null;
            }
        });
        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #F3F4F6;");
                }
            }
        });

        // Time spinners
        Spinner<Integer> startinghour = new Spinner<>(0, 23, 10);
        Spinner<Integer> startingmin  = new Spinner<>(0, 59, 0);
        Spinner<Integer> durationhour = new Spinner<>(0, 12, 3);
        Spinner<Integer> durationmin  = new Spinner<>(0, 59, 0);

        for (Spinner<Integer> sp : List.of(startinghour, startingmin, durationhour, durationmin)) {
            sp.setPrefHeight(38);
            sp.setPrefWidth(80);
            sp.setMaxWidth(80);
            sp.setStyle(spinnerStyle());
        }

        VBox shBox = timeSpinnerBox("Start Hr",  startinghour);
        VBox smBox = timeSpinnerBox("Start Min", startingmin);
        VBox dhBox = timeSpinnerBox("Dur Hr",    durationhour);
        VBox dmBox = timeSpinnerBox("Dur Min",   durationmin);

        HBox timeRow = new HBox(10, shBox, smBox, dhBox, dmBox);
        timeRow.setAlignment(Pos.CENTER_LEFT);

        // Add all field groups
        formFields.getChildren().addAll(
                fieldGroup2("Session",   sessionRow),
                fieldGroup2("Exam Date", datePicker),
                fieldGroup2("Exam Time", timeRow)
        );

        // Next button
        Button next = new Button("Continue to Room Selection  →");
        next.setDisable(true);
        next.setPrefHeight(44);
        next.setPrefWidth(260);
        next.setMaxWidth(260);
        next.setStyle("""
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 800;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        """);
        next.setOnMouseEntered(e -> next.setStyle("""
        -fx-background-color: #003FA3;
        -fx-text-fill: white;
        -fx-font-weight: 800;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        """));
        next.setOnMouseExited(e -> next.setStyle(
                next.isDisable() ? disabledBtnStyle() : activeBtnStyle()
        ));

        VBox btnBox = new VBox(next);
        btnBox.setPadding(new Insets(0, 28, 24, 28));
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        // Validation
        Runnable validate = () -> {
            boolean valid = selectedFile != null
                    && month.getValue() != null
                    && !month.getValue().isEmpty()
                    && year.getValue() != null
                    && !year.getValue().isEmpty()
                    && datePicker.getValue() != null;
            next.setDisable(!valid);
            next.setStyle(valid ? activeBtnStyle() : disabledBtnStyle());
        };

        month.valueProperty().addListener((obs, o, n) -> validate.run());
        year.valueProperty().addListener((obs, o, n)  -> validate.run());
        datePicker.valueProperty().addListener((obs, o, n) -> validate.run());

        next.setOnAction(e -> {
            ExamConfig config = new ExamConfig();
            if (selectedFile != null) {
                config.setFileName(selectedFile.getName());
                config.setFilePath(selectedFile.getAbsolutePath());
            }

            // Exam time build karo
            String examTime = String.format("%02d:%02d", startinghour.getValue(), startingmin.getValue())
                    + " - "
                    + String.format("%02d:%02d",
                    (startinghour.getValue() + durationhour.getValue()) % 24,
                    (startingmin.getValue()  + durationmin.getValue())  % 60);
            config.setExamTime(examTime);
            config.setSession(month.getValue() + "-" + year.getValue());
            config.setDate(datePicker.getValue());

            ExcelWork excelWork = new ExcelWork();
            ArrayList<String> result = excelWork.fatchExcel(selectedFile.getAbsolutePath());
            SharedData.totalStudents = Integer.parseInt(result.get(0));
            SharedData.totalStudentsLabel.setText(String.valueOf(SharedData.totalStudents));
            SharedData.subjects = result.subList(1, result.size());

            app.switchCenter(roomTableScreen(app, config));
        });

        formCard.getChildren().addAll(formHeader, formFields, btnBox);

        twoCol.getChildren().addAll(card, formCard);

        HBox.setHgrow(card, Priority.NEVER);
        card.setMaxHeight(Double.MAX_VALUE);

        twoCol.setMaxWidth(820);

        pageRoot.getChildren().addAll(pageHeader, twoCol);
        pageRoot.setAlignment(Pos.CENTER);
        mainScroll.setContent(pageRoot);
        layout.setCenter(mainScroll);

        return layout;
    }

// ── HELPER METHODS ──────────────────────────────────────

    private static VBox fieldGroup2(String label, javafx.scene.Node field) {
        VBox group = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("""
        -fx-font-size: 12px;
        -fx-font-weight: 700;
        -fx-text-fill: #374151;
        """);
        group.getChildren().addAll(lbl, field);
        return group;
    }

    private static VBox timeSpinnerBox(String label, Spinner<Integer> spinner) {
        VBox box = new VBox(6);
        Label lbl = new Label(label);
        lbl.setStyle("""
        -fx-font-size: 11px;
        -fx-font-weight: 700;
        -fx-text-fill: #6B7280;
        """);
        box.getChildren().addAll(lbl, spinner);
        VBox.setVgrow(spinner, Priority.ALWAYS);
        return box;
    }

    private static String comboStyle() {
        return """
        -fx-background-color: white;
        -fx-border-color: #E5E7EB;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-padding: 4 8 4 8;
        -fx-font-size: 13px;
        -fx-text-fill: #111827;
        """;
    }

    private static String spinnerStyle() {
        return """
        -fx-background-color: white;
        -fx-border-color: #E5E7EB;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-font-size: 13px;
        """;
    }

    private static String activeBtnStyle() {
        return """
        -fx-background-color: #0056D2;
        -fx-text-fill: white;
        -fx-font-weight: 800;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        """;
    }

    private static String disabledBtnStyle() {
        return """
        -fx-background-color: #D1D5DB;
        -fx-text-fill: #9CA3AF;
        -fx-font-weight: 800;
        -fx-font-size: 14px;
        -fx-background-radius: 10;
        """;
    }

    public static void openFileChooser(HomePage app) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input File");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showOpenDialog(null);

        Alert alert;

        if (file != null) {
            selectedFile = file;  //  STORE FILE

            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("File Selected: " + file.getName()); //  show file name
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("File Not Selected");
        }

        alert.showAndWait();
    }


}
