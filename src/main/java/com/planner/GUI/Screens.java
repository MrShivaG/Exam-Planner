package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.planner.Arrangement.Arrange;
import com.planner.Database.DB_Methods;
import com.planner.ExcelM.ExcelWork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalTime;
import java.util.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import static com.planner.Database.ArrangementsDB.fetcharrData;
import static org.apache.poi.ss.util.DateParser.parseDate;

public class Screens {
    public static int totalStudents = 0;
    public static Label totalStudentsLabel = new Label("0");
    public static List<String> subjects = new ArrayList<>();
    private static File selectedFile = null;
    static DB_Methods dbMethods;
    public static Map<Integer, List<Teacher>> roomTeachers = new HashMap<>();

    static {
        try {
            dbMethods = new DB_Methods();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //  DATA SCREEN
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


        Label session = new Label("Enter Session");
        session.getStyleClass().add("form-label");

        TextField textField = new TextField();
        textField.setPromptText("2025-26");
        textField.getStyleClass().add("input-field");

        Label label1 = new Label("Enter Date");
        label1.getStyleClass().add("form-label");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

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
        timeLabel.getStyleClass().add("form-label");

        TextField timeField = new TextField();
        timeField.setPromptText("10:00 - 01:00");
        timeField.getStyleClass().add("input-field");


        Label subjectLabel = new Label("Subject");
        subjectLabel.getStyleClass().add("form-label");

        TextField subjectField = new TextField();
        subjectField.setPromptText("CS-401");
        subjectField.getStyleClass().add("input-field");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.setHgap(15);
        gridPane.setVgap(18);
        gridPane.add(label2, 0, 0);
        gridPane.add(textField2, 1, 0);

        gridPane.add(collegeLabel, 0, 1);
        gridPane.add(collegeField, 1, 1);

        gridPane.add(session, 0, 2);
        gridPane.add(textField, 1, 2);

        gridPane.add(label1, 0, 3);
        gridPane.add(datePicker, 1, 3);

        gridPane.add(timeLabel, 0, 4);
        gridPane.add(timeField, 1, 4);

        gridPane.add(subjectLabel, 0, 5);
        gridPane.add(subjectField, 1, 5);

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
                            !textField.getText().isEmpty() &&
                            datePicker.getValue() != null &&
                            !textField2.getText().isEmpty();

            next.setDisable(!valid);
        };

        textField.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> validate.run());
        textField2.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        next.setOnAction(e -> {

            ExamConfig config = new ExamConfig();

            if (selectedFile != null) {
                config.setFileName(selectedFile.getName());
                config.setFilePath(selectedFile.getAbsolutePath());
            }
            config.setArrangementName(textField2.getText());
            config.setCollegeName(collegeField.getText());
            config.setExamTime(timeField.getText());
            config.setSession(textField.getText());
            config.setSubject(subjectField.getText());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            config.setDate(datePicker.getValue());

            String sessions = textField.getText();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate == null) {
                Notification.message("Please select a date!");
                return;
            }

            String name = textField2.getText();

            if (selectedFile == null ||
                    sessions.isEmpty() ||
                    datePicker.getValue() == null ||
                    name.isEmpty()) {

                System.out.println("All Entries Not Filled!");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incomplete Data");
                alert.setContentText("Please fill all fields and upload file.");
                alert.showAndWait();

                return;
            }


            ExcelWork excelWork = new ExcelWork();

            ArrayList<String> result = excelWork.fatchExcel(selectedFile.getAbsolutePath());
            Screens.totalStudents = Integer.parseInt(result.get(0));
            Screens.totalStudentsLabel.setText(String.valueOf(Screens.totalStudents));
            Screens.subjects = result.subList(1, result.size());

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

    public static Node dashboardContent(HomePage app) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_LEFT);

        int totalRoom = 0;
        int capacity = 0;
        boolean isConnected = false;

        try {
            if (dbMethods != null && dbMethods.con != null) {
                totalRoom = dbMethods.totalroom();
                capacity = dbMethods.totalcapacity();
                isConnected = true;
            }
        } catch (Exception e) {
            isConnected = false;
        }

        Label heading = new Label("System Overview");
        heading.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label sub = new Label("Institutional performance for All Semester 2026.");
        sub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13;-fx-font-style: italic;");

        VBox header = new VBox(5, heading, sub);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(createStatCard("TOTAL ROOMS", String.valueOf(totalRoom), "842 Completed", "442 Pending"), 0, 0);
        grid.add(createStatCard("TOTAL CAPACITY", String.valueOf(capacity), "+24 this week", ""), 1, 0);
        grid.add(createDatabaseStatusCard(), 2, 0);

        VBox setingbox = new VBox(10);
        setingbox.setPadding(new Insets(15));
        setingbox.setPrefSize(500, 400);
        setingbox.setMaxWidth(Double.MAX_VALUE);
        setingbox.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #c0c8d0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        HBox seatingHeader = new HBox();
        seatingHeader.setAlignment(Pos.CENTER_LEFT);
        seatingHeader.setPadding(new Insets(0, 0, 10, 0));
        seatingHeader.setStyle(
                "-fx-border-color: transparent transparent #e0e0e0 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label seatingHeaderLabel = new Label("Seating Arrangement");
        seatingHeaderLabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        seatingHeader.getChildren().add(seatingHeaderLabel);

        VBox cardsContainer = new VBox(10);

        if (isConnected) {
            try {
                List<String[]> arrangement = dbMethods.fetch_Arr_data();
                for (String[] data : arrangement) {
                    HBox card = databox(data[0], data[1], data[2], data[3], data[4]);
                    cardsContainer.getChildren().add(card);
                }
            } catch (Exception e) {
                cardsContainer.getChildren().add(new Label("Data unavailable"));
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        setingbox.getChildren().addAll(seatingHeader, scrollPane);

        root.getChildren().addAll(header, grid, setingbox);

        return root;
    }
    public static HBox databox(String arr_table_name,  String date, String capacity, String session,  String student){
        //room no. box
        VBox roomnobox = new VBox();
        roomnobox.setPadding(new Insets(5,5,5,5));
        roomnobox.getStyleClass().add("cardrow");
        roomnobox.setAlignment(Pos.CENTER);
        roomnobox.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        Label roomnolable = new Label("Room No.");
        roomnolable.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #6B7280;"
        );
        int lastDash = arr_table_name.lastIndexOf("_");
        String roomNo = arr_table_name.substring(lastDash + 1);

        Label roomnodata = new Label(roomNo);
        roomnodata.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;"
        );
        roomnobox.getChildren().addAll(roomnolable,roomnodata);

        //Date and time
        VBox dateVbox = new VBox();

        HBox datebox = new HBox();
        Label datelable = new Label("DATE - ");
        datelable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 14px;"
        );
        Label datedata = new Label(""+date);
        datedata.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;"
        );
        datebox.setAlignment(Pos.CENTER);
        datebox.setPadding(new Insets(2, 0, 2, 0));
        datebox.getChildren().addAll(datelable,datedata);

        String time = "10:00 - 01:00";
        Label timelable = new Label("            10:00 - 01:00");
        timelable.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #6B7280;"
        );

        dateVbox.getChildren().addAll(datebox,timelable);
        //date and time end

        String examStatus = getExamStatus(date,time);
        HBox statusbox = new HBox();
        statusbox.setAlignment(Pos.CENTER);
        Label status = new Label("Status - ");
        status.setStyle("-fx-text-fill: #1a1a2e;" +
                "-fx-font-size: 18px;");

        Label statuslable = new Label();
        statuslable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;" +
                        "-fx-font-style: italic;"
        );
        if (examStatus.equals("Today")) {
            statuslable.setText("HAPPENING TODAY");
            statuslable.setStyle("-fx-text-fill: #EF4444;" +
                    "-fx-font-size: 14px;"+
                    " -fx-font-weight: bold;" +
                    "-fx-font-style: italic;");
            statusbox.getChildren().addAll(status,statuslable);
        } else if (examStatus.equals("Completed")) {
            statuslable.setText("COMPLETED");
            statuslable.setStyle("-fx-text-fill: #10B981;" +
                    "-fx-font-size: 18px;"+
                    " -fx-font-weight: bold;" +
                    "-fx-font-style: italic;");
            statusbox.getChildren().addAll(status,statuslable);

        } else {
            statuslable.setText(examStatus);
            statusbox.getChildren().addAll(status,statuslable);
        }


        //capacity
        VBox capacitybox = new VBox();
        Label capacitylable = new Label("Capacity");
        capacitylable.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-text-fill: #6B7280;"
        );
        Label capacitydata = new Label(""+student+"/"+capacity);
        capacitydata.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;"
        );
        capacitybox.getChildren().addAll(capacitylable,capacitydata);
        //capacity end

        Button arrButton = new Button(">");
        arrButton.setStyle("-fx-font-size: 18px;" +
                "-fx-font-weight: bold;");

        arrButton.setOnAction(e ->{
            Node node = ArrTableView.show(arr_table_name, roomNo, date);
            HomePage.rightSide.setCenter(node);
        });



        HBox arrrowBox = new HBox();
        arrrowBox.getStyleClass().add("cardrow");
        arrrowBox.setSpacing(30);
        arrrowBox.setPadding(new Insets(5,20,5,20));
        arrrowBox.setAlignment(Pos.CENTER_RIGHT);
        arrrowBox.setStyle("-fx-background-color: #F8F9FA;"+
                "-fx-background-radius: 10;" +
                "-fx-border-color: #c0c8df;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;");


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        arrrowBox.getChildren().addAll(roomnobox,dateVbox,spacer,statusbox,capacitybox,arrButton);

        return arrrowBox;
    }

    public static String getExamStatus(String dateStr, String timeRange) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        try {
            LocalDate examDate = DateUtil.parse(dateStr);
            LocalDate today = LocalDate.now();

            String startTimeStr = timeRange.split("-")[0].trim();

            LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime now = LocalTime.now();

            if (examDate.isBefore(today)) {
                return "Completed";
            }
            else if (examDate.equals(today)) {

                if (now.isAfter(startTime)) {
                    return "Completed";
                } else {
                    return "Today";
                }
            }
            else if (examDate.equals(today.plusDays(1))) {
                return "Tomorrow";
            }
            else {
                long daysLeft = ChronoUnit.DAYS.between(today, examDate);
                return "In " + daysLeft + " days";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Format";
        }
    }

    private static VBox createStatCard(String title, String value, String bottomLeft, String bottomRight) {

        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(180);

        card.getStyleClass().add("card");

        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14;");

        Label number = new Label(value);
        number.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label left = new Label(bottomLeft);
        left.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

        Label right = new Label(bottomRight);
        right.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

        footer.getChildren().addAll(left, right);

        card.getChildren().addAll(label, number, footer);

        return card;
    }
    private static VBox createDatabaseStatusCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(180);
        card.getStyleClass().add("card");

        boolean isConnected = false;

        try (Connection c = ArrangementsDB.connection()) {
            isConnected = true;
        } catch (Exception e) {
            isConnected = false;
        }

        Label label = new Label("DATABASE STATUS");
        label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14;");

        Label statusText = new Label(isConnected ? "ONLINE" : "OFFLINE");
        String statusColor = isConnected ? "#10B981" : "#EF4444";
        statusText.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
        dot.setFill(javafx.scene.paint.Color.web(statusColor));

        Label subText = new Label(isConnected ? "System is synced" : "Connection failed");
        subText.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

        footer.getChildren().addAll(dot, subText);
        card.getChildren().addAll(label, statusText, footer);
        if (isConnected) {
            dot.getStyleClass().add("status-dot-online");

            ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.8), dot);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.5);
            pulse.setToY(1.5);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);


            FadeTransition fade = new FadeTransition(Duration.seconds(0.8), dot);
            fade.setFromValue(1.0);
            fade.setToValue(0.5);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);


            ParallelTransition pt = new ParallelTransition(pulse, fade);
            pt.play();
        } else {

            dot.getStyleClass().add("status-dot-offline");
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.8), dot);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.5);
            pulse.setToY(1.5);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Animation.INDEFINITE);


            FadeTransition fade = new FadeTransition(Duration.seconds(0.8), dot);
            fade.setFromValue(1.0);
            fade.setToValue(0.5);
            fade.setAutoReverse(true);
            fade.setCycleCount(Animation.INDEFINITE);


            ParallelTransition pt = new ParallelTransition(pulse, fade);
            pt.play();
        }
        if (!isConnected) {
            card.setStyle(card.getStyle() + "-fx-background-color: #FEE2E2;");
        }else {
            card.setStyle(card.getStyle() + "-fx-background-color: #DCFCE7;");
        }

        return card;
    }

    public static boolean autoAssignTeachers(List<Room> selectedRooms) {
        try {
            DB_Methods db = new DB_Methods();

            List<Teacher> maleList = db.getTeachersByGender("Male");
            List<Teacher> femaleList = db.getTeachersByGender("Female");

            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);

            int rooms = selectedRooms.size();

            for (int i = 0; i < selectedRooms.size(); i++) {

                Room room = selectedRooms.get(i);

                Teacher male = maleList.get(i);
                Teacher female = femaleList.get(i);

                List<Teacher> list = new ArrayList<>();
                list.add(male);
                list.add(female);

                roomTeachers.put(room.getRoomNo(), list);
            }

            Collections.shuffle(maleList);
            Collections.shuffle(femaleList);

            roomTeachers.clear();


            for (int i = 0; i < rooms; i++) {
                Room room = selectedRooms.get(i);

                List<Teacher> list = new ArrayList<>();
                list.add(maleList.get(i));
                list.add(femaleList.get(i));

                roomTeachers.put(room.getRoomNo(), list);
            }

            return true;

        } catch (Exception e) {
            Notification.message("Error assigning teachers");
            return false;
        }
    }

    public static VBox roomTableScreen(HomePage app, ExamConfig config) {

        ObservableList<Room> selectedRooms = FXCollections.observableArrayList();

        //Label selectedRoomsLabel = new Label("0");
        Label selectedCapacityLabel = new Label("0");
        Label statusLabel = new Label();
        statusLabel.setText("Select rooms to check capacity");
        statusLabel.setStyle("-fx-text-fill: #6B7280;" +
                " -fx-font-size: 12;");

        ListView<Room> selectedList = new ListView<>(selectedRooms);
        selectedList.getStyleClass().add("list-view");
        selectedList.setPrefWidth(250);

        selectedList.setCellFactory(lv -> {

            ListCell<Room> cell = new ListCell<>() {

                @Override
                protected void updateItem(Room room, boolean empty) {
                    super.updateItem(room, empty);

                    if (empty || room == null) {
                        setGraphic(null);
                    } else {
                        Label index = new Label((getIndex() + 1) + ".");
                        index.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");

                        Label name = new Label("Room " + room.getRoomNo());
                        name.setStyle("-fx-font-weight: bold;");

                        Label cap = new Label("Cap: " + room.getCapacity());
                        cap.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

                        VBox textBox = new VBox(name, cap);
                        textBox.setSpacing(3);

                        HBox row = new HBox(10, index, textBox);
                        row.setAlignment(Pos.CENTER_LEFT);

                        setGraphic(row);
                    }
                }
            };

            //  DRAG START
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {

                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);

                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(String.valueOf(cell.getIndex()));

                    db.setContent(cc);

                    event.consume();
                }
            });

            //  DRAG OVER
            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            //  DROP
            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();

                if (db.hasString()) {

                    int draggedIndex = Integer.parseInt(db.getString());
                    Room draggedItem = selectedList.getItems().remove(draggedIndex);

                    int dropIndex;

                    if (cell.isEmpty()) {
                        dropIndex = selectedList.getItems().size();
                    } else {
                        dropIndex = cell.getIndex();
                    }

                    selectedList.getItems().add(dropIndex, draggedItem);

                    event.setDropCompleted(true);
                    selectedList.getSelectionModel().select(dropIndex);
                } else {
                    event.setDropCompleted(false);
                }

                event.consume();
            });

            cell.setOnDragEntered(e -> cell.setStyle("-fx-background-color: #E8F0FE;"));
            cell.setOnDragExited(e -> cell.setStyle(""));

            return cell;
        });


        Button removeBtn = new Button("Remove");

        removeBtn.setOnAction(e -> {
            Room selected = selectedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedRooms.remove(selected);
            }
        });

        Button assignBtn = new Button("Assign Teacher");
        assignBtn.getStyleClass().add("primary-btn");

        assignBtn.setOnAction(e -> {

            if (selectedRooms.isEmpty()) {
                Notification.message("Please select rooms first");
                return;
            }

            boolean ok = Screens.autoAssignTeachers(selectedRooms);

            if (ok) {
                Notification.message("Teachers auto-assigned successfully!");
            }
        });

        Button generateBtn = new Button("Confirm Seating");
        generateBtn.getStyleClass().add("primary-btn");

        generateBtn.setOnAction(e -> {

            int[] roomsArray = selectedRooms.stream()
                    .mapToInt(Room::getRoomNo)
                    .toArray();

            if (roomsArray == null || roomsArray.length == 0) {
                System.out.println("No rooms selected!");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Room Selected");
                alert.setHeaderText("Selection Required");
                alert.setContentText("Please select at least one room before generating seating.");

                alert.showAndWait();
                return;
            }

            int totalCapacity = selectedRooms.stream()
                    .mapToInt(Room::getCapacity)
                    .sum();

            int totalStudents = Screens.totalStudents;

            if (totalCapacity < totalStudents) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Insufficient Capacity");
                alert.setHeaderText("Not Enough Seats");

                alert.setContentText(
                        "Total Students: " + totalStudents +
                                "\nTotal Room Capacity: " + totalCapacity +
                                "\n\nPlease select more rooms."
                );

                alert.showAndWait();
                return;
            }

            app.switchCenter(
                    ConfirmScreen(app, config, selectedRooms, config.getFileName())
            );
        });

        Label selectedTitle = new Label("Selected Rooms (Priority Wise): 0");
        VBox rightPanel = new VBox(10,
                selectedTitle,
                selectedList,
                new HBox(10),
                removeBtn,
                assignBtn,
                generateBtn
        );

        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(280);
        rightPanel.getStyleClass().add("card");
        rightPanel.setSpacing(10);


        FlowPane subjectPane = new FlowPane();
        subjectPane.setHgap(10);
        subjectPane.setVgap(10);
        subjectPane.setPadding(new Insets(10));
        TableView<Room> table = new TableView<>();
        for (String sub : Screens.subjects) {

            Label chip = new Label(sub);

            chip.setStyle(
                    "-fx-background-color: #EEF4FF;" +
                            "-fx-text-fill: #2563EB;" +
                            "-fx-padding: 6 12;" +
                            "-fx-background-radius: 15;" +
                            "-fx-font-weight: bold;"
            );

            subjectPane.getChildren().add(chip);
        }


        // Columns
        TableColumn<Room, Integer> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        roomCol.setPrefWidth(150);
        roomCol.setMaxWidth(150);

        TableColumn<Room, Integer> rowCol = new TableColumn<>("Rows");
        rowCol.setCellValueFactory(new PropertyValueFactory<>("rows"));
        rowCol.setPrefWidth(150);
        rowCol.setMaxWidth(150);

        TableColumn<Room, Integer> colCol = new TableColumn<>("Columns");
        colCol.setCellValueFactory(new PropertyValueFactory<>("columns"));
        colCol.setPrefWidth(150);
        colCol.setMaxWidth(150);

        TableColumn<Room, Integer> capCol = new TableColumn<>("Capacity");
        capCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        capCol.setPrefWidth(150);
        capCol.setMaxWidth(150);

        TableColumn<Room, Boolean> selectCol = new TableColumn<>("Select");

        selectCol.setCellValueFactory(cellData ->
                cellData.getValue().selectedProperty()
        );

        selectCol.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());

                    room.selectedProperty().set(checkBox.isSelected());

                    if (checkBox.isSelected()) {
                        if (!selectedRooms.contains(room)) {
                            selectedRooms.add(room);
                        }
                    } else {
                        selectedRooms.remove(room);
                    }
                    //selectedRoomsLabel.setText(String.valueOf(selectedRooms.size()));
                    selectedTitle.setText(
                            "Selected Rooms (Priority Wise): " + selectedRooms.size()
                    );

                    int selectedCapacity = selectedRooms.stream()
                            .mapToInt(Room::getCapacity)
                            .sum();

                    if (selectedCapacity >= Screens.totalStudents) {
                        statusLabel.setText(" Enough Capacity");
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        statusLabel.setText(" Not Enough Capacity");
                        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }

                    selectedCapacityLabel.setText(String.valueOf(selectedCapacity));
                });

            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Room room = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(room.isSelected());
                    setGraphic(checkBox);
                }
            }
        });
        selectCol.setEditable(true);

        table.setEditable(true);

        roomCol.setStyle("-fx-alignment: CENTER;");
        capCol.setStyle("-fx-alignment: CENTER;");
        rowCol.setStyle("-fx-alignment: CENTER;");
        colCol.setStyle("-fx-alignment: CENTER;");
        selectCol.setPrefWidth(75);
        selectCol.setMaxWidth(75);
        selectCol.setResizable(false);

        table.setMaxWidth(650);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(selectCol, roomCol, rowCol, colCol, capCol);

        // Load from DB
        try {
            DB_Methods db = new DB_Methods();
            List<int[]> rooms = db.fetchRowColumn();

            for (int[] r : rooms) {
                table.getItems().add(
                        new Room(r[0], r[1] * r[2], r[1], r[2])
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        int totalRooms = table.getItems().size();

        int totalCapacity = table.getItems().stream()
                .mapToInt(Room::getCapacity)
                .sum();

        HBox infoBar = new HBox(20);
        infoBar.setPadding(new Insets(10));
//
        VBox subjectsBox = new VBox(5);

// 2 column grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        int col = 0;
        int row = 0;

        for (String sub : Screens.subjects) {

            Label chip = new Label(sub);
            chip.setStyle(
                    "-fx-background-color: #EEF4FF;" +
                            "-fx-text-fill: #2563EB;" +
                            "-fx-padding: 4 8;" +
                            "-fx-background-radius: 10;" +
                            "-fx-font-size: 10;"
            );

            grid.add(chip, col, row);

            col++;
            if (col == 2) {
                col = 0;
                row++;
            }
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(80);
        scroll.setStyle("-fx-background-color: transparent;");

        subjectsBox.getChildren().add(scroll);

        VBox card4 = createInfoCard("Subjects", new Label(""));
        card4.getChildren().set(1, subjectsBox);

        VBox card1 = createInfoCard("Total Students", Screens.totalStudentsLabel);
        VBox card2 = createInfoCard("Total Rooms", new Label(String.valueOf(totalRooms)));
        VBox card3 = createInfoCard("Total Capacity", new Label(String.valueOf(totalCapacity)));
        //  VBox card4 = createInfoCard("Selected Rooms", selectedRoomsLabel);
        VBox card5 = createInfoCard("Selected Capacity", selectedCapacityLabel);
        VBox card6 = createInfoCard("Status", statusLabel);


        infoBar.getChildren().addAll(card2, card3, card1, card4, card5, card6);

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);

                if (room != null && room.isSelected()) {
                    setStyle("-fx-background-color  : #E8F0FE;");
                } else {
                    setStyle("");
                }
            }

            {
                setOnMouseClicked(e -> {
                    if (!isEmpty()) {
                        Room selected = getItem();
                        System.out.println("Selected Room: " + selected.getRoomNo());
                    }
                });
            }
        });

        HBox layout = new HBox(20, table, rightPanel);
        layout.setPadding(new Insets(20));

        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(infoBar, subjectPane, layout);
        vBox.setPadding(new Insets(20));

        return vBox;
    }

    private static VBox createInfoCard(String title, Label valueLabel) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);

        card.getStyleClass().add("card");

        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");

        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(160);
        valueLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        //valueLabel.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(t, valueLabel);

        return card;
    }

    public static VBox ConfirmScreen(
            HomePage app,
            ExamConfig config,
            List<Room> selectedRooms,
            String fileName
    ){
        VBox main = new VBox(20);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: #F8F9FA;");

        Label title = new Label("Confirm Arrangement Details");
        title.getStyleClass().add("title");

        GridPane info = new GridPane();
        info.setVgap(10);
        info.setHgap(20);

        info.add(new Label("Arrangement Name:"), 0, 0);
        info.add(new Label(config.getArrangementName()), 1, 0);

        info.add(new Label("College:"), 0, 1);
        info.add(new Label(config.getCollegeName()), 1, 1);

        info.add(new Label("Session:"), 0, 2);
        info.add(new Label(config.getSession()), 1, 2);

        info.add(new Label("Date:"), 0, 3);
        info.add(new Label(DateUtil.formatForUI(config.getDate())), 1, 3);

        info.add(new Label("Time:"), 0, 4);
        info.add(new Label(config.getExamTime()), 1, 4);

        info.add(new Label("Subject:"), 0, 5);
        info.add(new Label(config.getSubject()), 1, 5);

        info.add(new Label("Excel File:"), 0, 6);
        info.add(new Label(fileName), 1, 6);

        VBox infoCard = new VBox(info);
        infoCard.getStyleClass().add("card");

        VBox roomBox = new VBox(10);

        int totalCapacity = 0;

        for (Room room : selectedRooms) {
            Label r = new Label(
                    "Room " + room.getRoomNo() +
                            " | Rows: " + room.getRows() +
                            " | Columns: " + room.getColumns() +
                            " | Capacity: " + room.getCapacity()
            );
            roomBox.getChildren().add(r);
            totalCapacity += room.getCapacity();
        }

        Label totalCap = new Label("Total Capacity: " + totalCapacity);

        roomBox.getChildren().add(totalCap);

        VBox roomCard = new VBox(roomBox);
        roomCard.getStyleClass().add("card");

        Label students = new Label(
                "Total Students: " + Screens.totalStudentsLabel.getText()
        );

        Button confirmBtn = new Button("Confirm & Generate");
        confirmBtn.getStyleClass().add("primary-btn");

        Button backBtn = new Button("Back");

        confirmBtn.setOnAction(e -> {

            Arrange arrange = new Arrange();

            int[] roomsArray = selectedRooms.stream()
                    .mapToInt(Room::getRoomNo)
                    .toArray();

            try {
                ArrayList<String> tables = arrange.arrange(
                        roomsArray,
                        DateUtil.formatForDB(config.getDate()),
                        config.getSession()
                );

                app.switchCenter(
                        Gen_seat.showTablesScreen(tables, config)
                );

                System.out.println("Seating Generated Successfully!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Seating Generated Successfully!");
                alert.showAndWait();

            } catch (Exception ex) {
                Notification.message("Error generating seating");
            }
        });

        backBtn.setOnAction(e -> {
            app.switchCenter(Screens.roomTableScreen(app, config));
        });

        HBox buttons = new HBox(10, backBtn, confirmBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        main.getChildren().addAll(title, infoCard, roomCard, students, buttons);

        return main;
    }

    public static BorderPane about(HomePage app) {

        BorderPane root = new BorderPane();

        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: #F8F9FA;");
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("About Developers");
        title.setStyle(
                "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label subtitle = new Label("Meet the team behind Seating Planner");
        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #6B7280;"
        );

        VBox titleBox = new VBox(5, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        HBox devRow = new HBox(20);
        devRow.setAlignment(Pos.CENTER);

        devRow.getChildren().addAll(
                developerCard("Developer 1", "CSE", "Section A"),
                developerCard("Developer 2", "CSE", "Section A"),
                developerCard("Developer 3", "CSE", "Section A")
        );

        VBox productBox = new VBox(10);
        productBox.setAlignment(Pos.CENTER);
        productBox.setPadding(new Insets(20, 0, 0, 0));

        Label productTitle = new Label("Crafted with precision and care");
        productTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label productDesc1 = new Label(
                "Seating Planner is designed to automate and simplify exam seating arrangements."
        );

        Label productDesc2 = new Label(
                "Built using JavaFX for modern UI and MySQL for efficient data handling."
        );

        Label productDesc3 = new Label(
                "Focused on performance, scalability, and clean user experience."
        );

        productDesc1.setStyle("-fx-text-fill: #6B7280;");
        productDesc2.setStyle("-fx-text-fill: #6B7280;");
        productDesc3.setStyle("-fx-text-fill: #6B7280;");

        productBox.getChildren().addAll(productTitle, productDesc1, productDesc2, productDesc3);


        Label footer = new Label("© Seating Planner");
        footer.setStyle(
                "-fx-text-fill: #9CA3AF;" +
                        "-fx-font-size: 11px;"
        );


        container.getChildren().addAll(titleBox, devRow, productBox, footer);

        root.setCenter(container);

        return root;
    }

    private static VBox developerCard(String name, String branch, String section) {

        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0.2, 0, 4);"
        );

        card.setOnMouseEntered(e ->
                card.setStyle(
                        "-fx-background-color: #f9fbff;" +
                                "-fx-background-radius: 16;" +
                                "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.15), 20, 0.2, 0, 4);"
                )
        );

        card.setOnMouseExited(e ->
                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 16;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0.2, 0, 4);"
                )
        );

        InputStream is = Screens.class.getResourceAsStream("/E-SAPlogo.jpg");

        if (is == null) {
            System.out.println("Image not found!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Image not Found");
            alert.setContentText("Please Give Correct Path.");
            alert.showAndWait();
        }

        Image image = new Image(is);

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(120);   // bigger size
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(false);

// Circle clip (perfect round)
        Circle clip = new Circle(60, 60, 60); // centerX, centerY, radius
        imageView.setClip(clip);
        Label nameLabel = new Label(name);
        nameLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label branchLabel = new Label(branch);
        branchLabel.setStyle(
                "-fx-text-fill: #6B7280;" +
                        "-fx-font-size: 12px;"
        );

        Label sectionLabel = new Label(section);
        sectionLabel.setStyle(
                "-fx-text-fill: #6B7280;" +
                        "-fx-font-size: 12px;"
        );

        card.getChildren().addAll(imageView, nameLabel, branchLabel, sectionLabel);

        return card;
    }
}