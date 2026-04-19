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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableView;

import static com.planner.Database.ArrangementsDB.fetcharrData;

public class Screens {
    public static int totalStudents = 0;
    public static Label totalStudentsLabel = new Label("0");
    public static List<String> subjects = new ArrayList<>();
    private static File selectedFile = null;

    static DB_Methods dbMethods;

    static {
        try {
            dbMethods = new DB_Methods();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Screens() throws SQLException {
    }

    //  ARRANGEMENT SCREEN
    public static BorderPane arrangementContent(HomePage app) {

        BorderPane layout = new BorderPane();

//        Button button = new Button("+ New Arrangement");
//        button.getStyleClass().add("button-primary");
//        button.setOnAction(e -> app.switchCenter(dataScreen(app)));

//        HBox topBox = new HBox(button);
//        topBox.setAlignment(Pos.CENTER_RIGHT);
//        topBox.setPadding(new Insets(20));

//        layout.setTop(topBox);

//        StackPane card = CardComponent.createCard(
//                "Data Input",
//                "Enter student data",
//                "/input.png",
//                () -> app.switchCenter(dataScreen(app))
//        );

        // layout.setLeft(app.switchCenter(dataScreen(app)));

        return layout;
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
        session.getStyleClass().add("hyyuser");

        TextField textField = new TextField();
        textField.setPromptText("2037-38");
        textField.getStyleClass().add("hyyuser");


        Label label1 = new Label("Enter Date");
        label1.getStyleClass().add("hyyuser");

        TextField textField1 = new TextField();
        textField1.setPromptText("Exam Date");
        textField1.getStyleClass().add("hyyuser");

        Label label2 = new Label("Arrangement Name");
        label2.getStyleClass().add("hyyuser");

        TextField textField2 = new TextField();
        textField2.setPromptText("i.e. Exam Arrangement");
        textField2.getStyleClass().add("hyyuser");


        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(20);
        gridPane.add(session, 0,0);
        gridPane.add(textField, 1, 0);
        gridPane.add(label1, 0,1);
        gridPane.add(textField1, 1,1);
        gridPane.add(label2, 0,2);
        gridPane.add(textField2, 1,2);

        Button next = new Button("Next ≫ ");
//        next.setDisable(true);
//        Runnable validate = () -> {
//            boolean valid =
//                    selectedFile != null &&
//                            !textField.getText().isEmpty() &&
//                            !textField1.getText().isEmpty() &&
//                            !textField2.getText().isEmpty();
//
//            next.setDisable(!valid);
//        };

//        textField.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
//        textField1.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
//        textField2.textProperty().addListener((obs, oldVal, newVal) -> validate.run());
        next.setOnAction(e -> {

            String sessions = textField.getText();
            String date = textField1.getText();
            String name = textField2.getText();

            //  VALIDATION
//            if (selectedFile == null ||
//                    sessions.isEmpty() ||
//                    date.isEmpty() ||
//                    name.isEmpty()) {
//
//                System.out.println("All Entries Not Filled!");
//
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("Incomplete Data");
//                alert.setContentText("Please fill all fields and upload file.");
//                alert.showAndWait();
//
//                return; //  STOP HERE
//            }

            //  PROCEED
            ExcelWork excelWork = new ExcelWork();

            ArrayList<String> result = excelWork.fatchExcel(selectedFile.getAbsolutePath());
            Screens.totalStudents = Integer.parseInt(result.get(0));
            Screens.totalStudentsLabel.setText(String.valueOf(Screens.totalStudents));
            Screens.subjects = result.subList(1, result.size());

            app.switchCenter(roomTableScreen(app));
        });
        next.getStyleClass().add("primary-btn");

        grid.add(card, 0, 0);
        grid.add(gridPane, 1, 0);
     //   grid.add(vBox1, 2, 0);
       // grid.add(vBox2, 3, 0);
        grid.add(next, 2, 1);
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

        Label sub = new Label("Institutional performance for Fall Semester 2024.");
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
        //room no box end


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

        arrButton.setOnAction(e ->
                ArrTableView.show(arr_table_name,roomNo,date)
        );



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

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        try {

            LocalDate examDate = LocalDate.parse(dateStr, dateFormatter);
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

        //  SMALL TITLE
        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14;");

        //  BIG NUMBER
        Label number = new Label(value);
        number.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        //  FOOTER
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

        try {
            if (dbMethods != null && dbMethods.con != null && !dbMethods.con.isClosed()) {
                isConnected = true;
            }
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

    public static VBox roomTableScreen(HomePage app) {

        ObservableList<Room> selectedRooms = FXCollections.observableArrayList();

        Label selectedRoomsLabel = new Label("0");
        Label selectedCapacityLabel = new Label("0");
        Label statusLabel = new Label();
        statusLabel.setText("Select rooms to check capacity");
        statusLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 6;");

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
            selectedRooms.remove(selected);
        });


        Button generateBtn = new Button("Generate Seating");
        generateBtn.getStyleClass().add("primary-btn");

        generateBtn.setOnAction(e -> {

            int[] roomsArray = selectedRooms.stream()
                    .mapToInt(Room::getRoomNo)
                    .toArray();


            if (roomsArray == null || roomsArray.length == 0) {
                System.out.println("No rooms selected!");
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("No Room Selected");
//                alert.setHeaderText("Selection Required");
//                alert.setContentText("Please select at least one room before generating seating.");
//
//                alert.showAndWait();
                return;
            }

            int totalCapacity = selectedRooms.stream()
                    .mapToInt(Room::getCapacity)
                    .sum();

            int totalStudents = 0;

//            try {
//                totalStudents = new FatchStudents().fatchStudent()
//                        .stream()
//                      //  .mapToInt(s -> s.getLength())
//                       // .sum();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }

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

            Arrange arrange = new Arrange();

            try {
                arrange.arrange(roomsArray, "2024-05-10");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Seating Generated Successfully!");

//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setContentText("Seating Generated Successfully!");
//            alert.showAndWait();

            Gen_seat.showtable("17_04_2026_301");

        });


        VBox rightPanel = new VBox(10,
                new Label("Selected Rooms (Priority)"),
                selectedList,
                new HBox(10),
                removeBtn,
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
                    selectedRoomsLabel.setText(String.valueOf(selectedRooms.size()));

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
        selectCol.setPrefWidth(50);
        selectCol.setMaxWidth(50);
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

        VBox card1 = createInfoCard("Total Students", Screens.totalStudentsLabel);
        VBox card2 = createInfoCard("Total Rooms", new Label(String.valueOf(totalRooms)));
        VBox card3 = createInfoCard("Total Capacity", new Label(String.valueOf(totalCapacity)));
        VBox card4 = createInfoCard("Selected Rooms", selectedRoomsLabel);
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

        valueLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        card.getChildren().addAll(t, valueLabel);

        return card;
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

        Region avatar = new Region();
        avatar.setPrefSize(80, 80);
        avatar.setStyle(
                "-fx-background-color: #E5E7EB;" +
                        "-fx-background-radius: 50;"
        );

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

        card.getChildren().addAll(avatar, nameLabel, branchLabel, sectionLabel);

        return card;
    }
}