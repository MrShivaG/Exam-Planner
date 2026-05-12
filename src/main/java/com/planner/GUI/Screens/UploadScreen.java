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

import static com.planner.GUI.Screens.RoomTableScreen.roomTableScreen;

public class UploadScreen {

    private static File selectedFile = null;

    public static BorderPane dataScreen(HomePage app) {

        BorderPane layout = new BorderPane();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(50);

        StackPane card = CardComponent.createCard(
                "Upload File",
                "Drag & Drop your File",
                "/upload.png",
                () -> openFileChooser(app)
        );


//        TextField month = new TextField();
//        month.setPromptText("June");
//        month.getStyleClass().add("input-field");

        Label session = new Label("Enter Session");

        session.setStyle("""
        -fx-font-size: 15px;
        -fx-font-weight: 700;
        -fx-text-fill: #111827;
        """);

        ComboBox<String> month = new ComboBox<>();

        month.getItems().addAll("June", "Dec");

        month.setPromptText("Month");

        month.setPrefWidth(120);

        month.getStyleClass().add("curator-combo");

//        month.lookup(".list-cell").setStyle("""
//        -fx-text-fill: #111827;
//        """);

        ComboBox<String> year = new ComboBox<>();

        int currentYear1 = java.time.Year.now().getValue() % 100;

        for (int i = currentYear1 - 3; i <= 99; i++) {

            year.getItems().add(String.format("%02d", i));
        }
        for (int i = 0; i < currentYear1 -3; i++) {

            year.getItems().add(
                    String.format("%02d", i)
            );
        }

        int currentYear2 = java.time.Year.now().getValue() % 100;

        year.setValue(String.format("%02d", currentYear2));

        year.setPromptText("Year");

        year.setEditable(true);

        year.setPrefWidth(120);

        year.getStyleClass().add("curator-combo");

//        year.lookup(".list-cell").setStyle("""
//        -fx-text-fill: #111827;
//        """);

        HBox hBox = new HBox(15);

        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().addAll(month, year);
//        TextField year = new TextField();
//        year.setPromptText("26");
//        year.getStyleClass().add("input-field");

        Label label1 = new Label("Enter Date");
        label1.getStyleClass().add("form-label");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setMaxWidth(135);
        datePicker.setPrefHeight(42);
        datePicker.getStyleClass().add("curator-combo");

        datePicker.setValue(LocalDate.now());

        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, formatter)
                        : null;
            }
        });

        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f0f0f0;");
                }
            }
        });

        Label label2 = new Label("Arrangement Name");
        label2.getStyleClass().add("form-label");

        TextField textField2 = new TextField();
        textField2.setPromptText("i.e. Exam Arrangement");
        textField2.getStyleClass().add("input-field");

        Label collegeLabel = new Label("College Name");
        collegeLabel.getStyleClass().add("form-label");

        TextField collegeField = new TextField();
        collegeField.setPromptText("SISTec-R");
        collegeField.getStyleClass().add("input-field");


        Label timeLabel = new Label("Exam Time");

        timeLabel.setStyle("""
        -fx-font-size: 15px;
        -fx-font-weight: 700;
        -fx-text-fill: #111827;
        """);

        Spinner<Integer> startinghour =
                new Spinner<>(0, 23, 10);

        Spinner<Integer> startingmin =
                new Spinner<>(0, 59, 0);

        Spinner<Integer> durationhour =
                new Spinner<>(0, 12, 3);

        Spinner<Integer> durationmin =
                new Spinner<>(0, 59, 0);

        startinghour.getStyleClass().add("curator-spinner");

        startingmin.getStyleClass().add("curator-spinner");

        durationhour.getStyleClass().add("curator-spinner");

        durationmin.getStyleClass().add("curator-spinner");

        startinghour.setPrefHeight(42);
        startingmin.setPrefHeight(42);
        durationhour.setPrefHeight(42);
        durationmin.setPrefHeight(42);
        startinghour.setMaxWidth(100);
        startingmin.setMaxWidth(100);
        durationhour.setMaxWidth(100);
        durationmin.setMaxWidth(100);

        VBox startHourBox = new VBox(5);

        Label sh = new Label("Start Hour");

        sh.setStyle("""
        -fx-font-size: 12px;
        -fx-text-fill: #6B7280;
        -fx-font-weight: 600;
        """);

        startHourBox.getChildren().addAll(sh, startinghour);


        VBox startMinBox = new VBox(5);

        Label sm = new Label("Start Minute");

        sh.setStyle("""
        -fx-font-size: 12px;
        -fx-text-fill: #6B7280;
        -fx-font-weight: 600;
        """);

        startMinBox.getChildren().addAll(sm, startingmin);


        VBox durationHourBox = new VBox(5);

        Label dh = new Label("Duration Hour");

        sh.setStyle("""
        -fx-font-size: 12px;
        -fx-text-fill: #6B7280;
        -fx-font-weight: 600;
        """);

        durationHourBox.getChildren().addAll(dh, durationhour);


        VBox durationMinBox = new VBox(5);

        Label dm = new Label("Duration Minute");

        sh.setStyle("""
        -fx-font-size: 12px;
        -fx-text-fill: #6B7280;
        -fx-font-weight: 600;
        """);

        durationMinBox.getChildren().addAll(dm, durationmin);

        HBox hBox1 = new HBox(15);

        hBox1.setAlignment(Pos.CENTER_LEFT);

        hBox1.getChildren().addAll(
                startHourBox,
                startMinBox,
                durationHourBox,
                durationMinBox
        );

//        TextField timeField = new TextField();
//        timeField.setPromptText("10:00 - 01:00");
//        timeField.getStyleClass().add("input-field");



        Label subjectLabel = new Label("Subject");
        subjectLabel.getStyleClass().add("form-label");

        TextField subjectField = new TextField();
        subjectField.setPromptText("CS-401");
        subjectField.getStyleClass().add("input-field");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(15);
        gridPane.setVgap(18);
        //  gridPane.add(label2, 0, 0);
        //  gridPane.add(textField2, 1, 0);

        //   gridPane.add(collegeLabel, 0, 1);
        //  gridPane.add(collegeField, 1, 1);

        gridPane.add(session, 0, 2);
        gridPane.add(hBox, 1, 2);

        gridPane.add(label1, 0, 3);
        gridPane.add(datePicker, 1, 3);

        gridPane.add(timeLabel, 0, 4);
        gridPane.add(hBox1, 1, 4);

        // gridPane.add(subjectLabel, 0, 5);
        // gridPane.add(subjectField, 1, 5);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(250);

        gridPane.getColumnConstraints().addAll(col1, col2);

        Button next = new Button("Next ≫ ");
        next.setDisable(true);
        Runnable validate = () -> {
            boolean valid =
                    selectedFile != null &&
                            !month.getValue().isEmpty() &&
                            !year.getValue().isEmpty() &&
                            datePicker.getValue() != null ;

            next.setDisable(!valid);
        };

//        &&
//        !textField2.getText().isEmpty();

        month.itemsProperty().addListener((obs, oldVal, newVal) -> validate.run());
        year.itemsProperty().addListener((obs, oldVal, newVal) -> validate.run());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> validate.run());
        //  textField2.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        next.setOnAction(e -> {

            ExamConfig config = new ExamConfig();

            if (selectedFile != null) {
                config.setFileName(selectedFile.getName());
                config.setFilePath(selectedFile.getAbsolutePath());
            }
            config.setArrangementName(textField2.getText());
            config.setCollegeName(collegeField.getText());
         //   config.setExamTime(timeField.getText());
            config.setSession(month.getValue());
            config.setSession(year.getValue());
            config.setSubject(subjectField.getText());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            config.setDate(datePicker.getValue());

            String monthh = month.getValue();
            String yearr = year.getValue();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate == null) {
                Notification.message("Please select a date!");
                return;
            }

            String name = textField2.getText();

            //   ||name.isEmpty()

            if (selectedFile == null ||
                    monthh.isEmpty() ||
                    yearr.isEmpty() ||
                    datePicker.getValue() == null
            ) {

                System.out.println("All Entries Not Filled!");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incomplete Data");
                alert.setContentText("Please fill all fields and upload file.");
                alert.showAndWait();

                return;
            }


            ExcelWork excelWork = new ExcelWork();

            ArrayList<String> result = excelWork.fatchExcel(selectedFile.getAbsolutePath());
            SharedData.totalStudents = Integer.parseInt(result.get(0));
            SharedData.totalStudentsLabel.setText(String.valueOf(SharedData.totalStudents));
            SharedData.subjects = result.subList(1, result.size());

            app.switchCenter(roomTableScreen(app, config));
        });
        next.getStyleClass().add("primary-btn");

        VBox mainForm = new VBox(20, gridPane);
        mainForm.getStyleClass().add("card");
        mainForm.setPadding(new Insets(20));
        mainForm.getChildren().add(next);

        grid.add(card, 0, 0);
        grid.add(mainForm, 1, 0);
        //  grid.add(next, 2, 1);
        grid.setMaxWidth(1000);

        layout.setLeft(grid);
        layout.setMaxWidth(800);
        layout.setPadding(new Insets(0, 0, 0, 100));
        return layout;
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
