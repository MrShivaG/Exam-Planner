package com.planner.GUI;

import com.planner.Arrangement.Arrange;
import com.planner.Arrangement.FatchStudents;
import com.planner.Database.DB_Methods;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Screens {

    private static File selectedFile = null;
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

        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);

        Label session = new Label("Enter Session");
        session.getStyleClass().add("hyyuser");

        TextField textField = new TextField();
        textField.setPromptText("2037-38");
        textField.getStyleClass().add("hyyuser");

        vBox.getChildren().addAll(session, textField);

        VBox vBox1 = new VBox(20);
        vBox1.setAlignment(Pos.CENTER);

        Label label1 = new Label("Enter Date");
        label1.getStyleClass().add("hyyuser");

        TextField textField1 = new TextField();
        textField1.setPromptText("Exam Date");
        textField1.getStyleClass().add("hyyuser");

        vBox1.getChildren().addAll(label1, textField1);

        VBox vBox2 = new VBox(20);
        vBox2.setAlignment(Pos.CENTER);

        Label label2 = new Label("Arrangement Name");
        label2.getStyleClass().add("hyyuser");

        TextField textField2 = new TextField();
        textField2.setPromptText("i.e. Exam Arrangement");
        textField2.getStyleClass().add("hyyuser");

        vBox2.getChildren().addAll(label2, textField2);

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
            app.switchCenter(roomTableScreen(app));
        });
        next.getStyleClass().add("primary-btn");

        grid.add(card, 0, 0);
        grid.add(vBox, 1, 0);
        grid.add(vBox1, 2, 0);
        grid.add(vBox2, 3, 0);
        grid.add(next, 3,1);
        grid.setMaxWidth(1000);

        layout.setLeft(grid);
        layout.setMaxWidth(800);
        layout.setPadding(new Insets(0,0,0,100));
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

        //  TITLE SECTION
        Label heading = new Label("System Overview");
        heading.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        Label sub = new Label("Institutional performance for Fall Semester 2024.");
        sub.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 13;");

        VBox header = new VBox(5, heading, sub);

        //  CARDS GRID
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(createStatCard("TOTAL EXAMS", "1,284", "842 Completed", "442 Pending"), 0, 0);
        grid.add(createStatCard("TOTAL STUDENTS", "14,502", "+24 this week", ""), 1, 0);
        grid.add(createStatCard("ROOM UTILIZATION", "94%", "High Capacity Alert", ""), 2, 0);

        root.getChildren().addAll(header, grid);

        return root;
    }


    private static VBox createStatCard(String title, String value, String bottomLeft, String bottomRight) {

        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(180);

        card.getStyleClass().add("card");

        //  SMALL TITLE
        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

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

    public static VBox roomTableScreen(HomePage app) {

        Label totalstudent = new Label("Total Student");
        Label label1 = new Label("label1");
        Label label2 = new Label("label2");
        Label totalstudent1 = new Label("Total Student");
        Label label3 = new Label("label1");
        Label label4 = new Label("label2");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.add(totalstudent, 0, 0);
        grid.add(totalstudent1, 1, 0);
        grid.add(label1, 0, 1);
        grid.add(label2, 1, 1);
        grid.add(label3, 0, 2);
        grid.add(label4, 1, 2);
        grid.setHgap(50);
        grid.setVgap(20);



//       HBox hBox = new HBox(100);
//
//
//        hBox.getChildren().addAll(totalstudent, label1, label2);


        ObservableList<Room> selectedRooms = FXCollections.observableArrayList();
        ListView<Room> selectedList = new ListView<>(selectedRooms);
        selectedList.setPrefWidth(250);

        selectedList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                if (empty || room == null) {
                    setText(null);
                } else {
                    setText((getIndex() + 1) + ". Room " + room.getRoomNo());
                }
            }
        });

        Button upBtn = new Button("↑");
        Button downBtn = new Button("↓");

        upBtn.setOnAction(e -> {
            int index = selectedList.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                Collections.swap(selectedRooms, index, index - 1);
                selectedList.getSelectionModel().select(index - 1);
            }
        });

        downBtn.setOnAction(e -> {
            int index = selectedList.getSelectionModel().getSelectedIndex();
            if (index < selectedRooms.size() - 1) {
                Collections.swap(selectedRooms, index, index + 1);
                selectedList.getSelectionModel().select(index + 1);
            }
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

            arrange.setRooms(roomsArray);
            try {
                arrange.arrange("2024-05-10");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Seating Generated Successfully!");
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setContentText("Seating Generated Successfully!");
//            alert.showAndWait();
        });


        VBox rightPanel = new VBox(10,
                new Label("Selected Rooms (Priority)"),
                selectedList,
                new HBox(10, upBtn, downBtn),
                removeBtn,
                generateBtn
        );

        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(280);


        TableView<Room> table = new TableView<>();

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

        HBox layout = new HBox(20, table,rightPanel);
        layout.setPadding(new Insets(20));

        VBox vBox = new VBox(30);
        vBox.getChildren().addAll(grid, layout);

        return vBox;
    }
}




