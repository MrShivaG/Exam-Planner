package com.planner.GUI.Screens;

import com.planner.GUI.Screens.ConfirmScreen;
import com.planner.GUI.Screens.TeacherAssign;

import com.planner.Database.DB_Methods;
import com.planner.GUI.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class RoomTableScreen {

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
        removeBtn.getStyleClass().add("primary-btn");

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

            boolean ok = TeacherAssign.autoAssignTeachers(selectedRooms);

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

            int totalStudents = SharedData.totalStudents;

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
                    ConfirmScreen.show(app, config, selectedRooms, config.getFileName())
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


//        FlowPane subjectPane = new FlowPane();
//        subjectPane.setHgap(10);
//        subjectPane.setVgap(10);
//        subjectPane.setPadding(new Insets(10));
//        TableView<Room> table = new TableView<>();
//        for (String sub : Screens.subjects) {
//
//            Label chip = new Label(sub);
//
//            chip.setStyle(
//                    "-fx-background-color: #EEF4FF;" +
//                            "-fx-text-fill: #2563EB;" +
//                            "-fx-padding: 6 12;" +
//                            "-fx-background-radius: 15;" +
//                            "-fx-font-weight: bold;"
//            );
//
//            subjectPane.getChildren().add(chip);
//        }
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
                    //selectedRoomsLabel.setText(String.valueOf(selectedRooms.size()));
                    selectedTitle.setText(
                            "Selected Rooms (Priority Wise): " + selectedRooms.size()
                    );

                    int selectedCapacity = selectedRooms.stream()
                            .mapToInt(Room::getCapacity)
                            .sum();

                    if (selectedCapacity >= SharedData.totalStudents) {
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

        for (String sub : SharedData.subjects) {

            Label chip = new Label(sub);
            chip.setStyle(
                    "-fx-background-color: #EEF4FF;" +
                            "-fx-text-fill: #2563EB;" +
                            "-fx-padding: 0 8;" +
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

        VBox card1 = createInfoCard("Total Students", SharedData.totalStudentsLabel);
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
        vBox.getChildren().addAll(infoBar, layout); //, subjectPane
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

}
