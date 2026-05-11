package com.planner.GUI.Screens;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.Cursor;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

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

    public static ScrollPane roomTableScreen(HomePage app, ExamConfig config) {

        ObservableList<Room> selectedRooms = FXCollections.observableArrayList();

        TableView<Room> table = new TableView<>();

        final int[] requiredSeats = {SharedData.totalStudents};


        ToggleGroup arrangementGroup = new ToggleGroup();

        RadioButton linearBtn = new RadioButton("Use This Layout");
        RadioButton alternateStrictBtn = new RadioButton("Use This Layout");
        RadioButton alternateBalancedBtn = new RadioButton("Use This Layout");

        linearBtn.setToggleGroup(arrangementGroup);
        alternateStrictBtn.setToggleGroup(arrangementGroup);
        alternateBalancedBtn.setToggleGroup(arrangementGroup);

        alternateBalancedBtn.setSelected(true);

        Label selectedCapacityLabel = new Label("0 / " + requiredSeats[0]);
        Label selectedTitle = new Label("Selected Rooms (Priority Wise): 0");
        selectedTitle.setStyle(
                "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        //Label selectedRoomsLabel = new Label("0");
        Label statusLabel = new Label();
        statusLabel.setText("Select rooms to check capacity");
        statusLabel.setStyle("-fx-text-fill: #6B7280;" +
                " -fx-font-size: 12;");

        Runnable updateRequiredSeats = () -> {

            if (alternateStrictBtn.isSelected()) {

                requiredSeats[0] = calculateRequiredSeats();

            } else {

                requiredSeats[0] = SharedData.totalStudents;
            }

            int selectedCapacity = selectedRooms.stream()
                    .mapToInt(Room::getCapacity)
                    .sum();

            selectedCapacityLabel.setText(
                    selectedCapacity + " / " + requiredSeats[0]
            );

            if (selectedCapacity >= requiredSeats[0]) {

                statusLabel.setText(" Enough Capacity");

                statusLabel.setStyle(
                        "-fx-text-fill: green;" +
                                "-fx-font-weight: bold;"
                );

            } else {

                statusLabel.setText(" Not Enough Capacity");

                statusLabel.setStyle(
                        "-fx-text-fill: red;" +
                                "-fx-font-weight: bold;"
                );
            }

        };

        arrangementGroup.selectedToggleProperty().addListener(
                (obs, oldToggle, newToggle) -> {

                    updateRequiredSeats.run();
                }
        );
//        linearBtn.setOnAction(e -> updateRequiredSeats.run());
//        alternateStrictBtn.setOnAction(e -> updateRequiredSeats.run());
//        alternateBalancedBtn.setOnAction(e -> updateRequiredSeats.run());

        updateRequiredSeats.run();

        if (SharedData.prioritizedSubjects.isEmpty()) {

            SharedData.prioritizedSubjects.addAll(
                    SharedData.subjects
            );
        }

        Label subjectPriorityTitle =
                new Label("Subject Seating Priority");

        subjectPriorityTitle.setStyle(
                "-fx-font-size: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label subjectPriorityDesc =
                new Label(
                        "Arrange subjects in the order you want " +
                                "them to receive seating priority during " +
                                "seating generation."
                );

        subjectPriorityDesc.setWrapText(true);

        subjectPriorityDesc.setStyle(
                "-fx-text-fill: #6B7280;" +
                        "-fx-font-size: 13;"
        );

        ListView<String> subjectPriorityList =
                new ListView<>(SharedData.prioritizedSubjects);

        subjectPriorityList.setPrefHeight(260);

        subjectPriorityList.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;"


        );

        subjectPriorityList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {

                    super.updateItem(item, empty);

                    if (empty || item == null) {

                        setGraphic(null);

                    } else {

                        Label dragHandle = new Label("⋮⋮");

                        dragHandle.setStyle(
                                "-fx-text-fill: #9CA3AF;" +
                                        "-fx-font-size: 15;" +
                                        "-fx-font-weight: bold;"
                        );

                        Label subject = new Label(item);

                        subject.setStyle(
                                "-fx-font-size: 14;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-text-fill: #111827;"
                        );

                        Label subText =
                                new Label(
                                        "Included in seating arrangement"
                                );

                        subText.setStyle(
                                "-fx-text-fill: #6B7280;" +
                                        "-fx-font-size: 11;"
                        );

                        VBox textBox = new VBox(
                                subject,
                                subText
                        );

                        textBox.setSpacing(3);

                        Label badge = new Label();

                        if (getIndex() == 0) {

                            badge.setText("Highest Priority");

                            badge.setStyle(
                                    "-fx-background-color: #DCFCE7;" +
                                            "-fx-text-fill: #166534;" +
                                            "-fx-padding: 4 10;" +
                                            "-fx-background-radius: 30;" +
                                            "-fx-font-size: 10;" +
                                            "-fx-font-weight: bold;"
                            );

                        } else {

                            badge.setText(
                                    "Priority " + (getIndex() + 1)
                            );

                            badge.setStyle(
                                    "-fx-background-color: #EEF2FF;" +
                                            "-fx-text-fill: #3730A3;" +
                                            "-fx-padding: 4 10;" +
                                            "-fx-background-radius: 30;" +
                                            "-fx-font-size: 10;" +
                                            "-fx-font-weight: bold;"
                            );
                        }

                        Region spacer = new Region();

                        HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS
                        );

                        HBox row = new HBox(
                                14,
                                dragHandle,
                                textBox,
                                spacer,
                                badge
                        );

                        row.setAlignment(Pos.CENTER_LEFT);

                        VBox wrapper = new VBox(row);

                        wrapper.setPadding(new Insets(16));

                        wrapper.setStyle(
                                "-fx-background-color: white;" +
                                        "-fx-background-radius: 14;" +
                                        "-fx-border-color: #E5E7EB;" +
                                        "-fx-border-radius: 14;" +
                                        "-fx-border-width: 1;"
                        );

                        wrapper.setOnMouseEntered(e -> {

                            wrapper.setStyle(
                                    "-fx-background-color: #FAFAFA;" +
                                            "-fx-background-radius: 14;" +
                                            "-fx-border-color: #CBD5E1;" +
                                            "-fx-border-radius: 14;" +
                                            "-fx-border-width: 1;"
                            );
                        });

                        wrapper.setOnMouseExited(e -> {

                            wrapper.setStyle(
                                    "-fx-background-color: white;" +
                                            "-fx-background-radius: 14;" +
                                            "-fx-border-color: #E5E7EB;" +
                                            "-fx-border-radius: 14;" +
                                            "-fx-border-width: 1;"
                            );
                        });

                        setGraphic(wrapper);

                        setStyle(
                                "-fx-background-color: transparent;" +
                                        "-fx-padding: 6 0;"
                        );
                    }
                }
            };

            // DRAG START
            cell.setOnDragDetected(event -> {

                if (!cell.isEmpty()) {

                    Dragboard db =
                            cell.startDragAndDrop(
                                    TransferMode.MOVE
                            );

                    ClipboardContent cc =
                            new ClipboardContent();

                    cc.putString(
                            String.valueOf(cell.getIndex())
                    );

                    db.setContent(cc);

                    event.consume();
                }
            });

            // DRAG OVER
            cell.setOnDragOver(event -> {

                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString()) {

                    event.acceptTransferModes(
                            TransferMode.MOVE
                    );
                }

                event.consume();
            });

            // DROP
            cell.setOnDragDropped(event -> {

                Dragboard db = event.getDragboard();

                if (db.hasString()) {

                    int draggedIndex =
                            Integer.parseInt(db.getString());

                    String draggedItem =
                            subjectPriorityList
                                    .getItems()
                                    .remove(draggedIndex);

                    int dropIndex;

                    if (cell.isEmpty()) {

                        dropIndex =
                                subjectPriorityList
                                        .getItems()
                                        .size();

                    } else {

                        dropIndex = cell.getIndex();
                    }

                    subjectPriorityList
                            .getItems()
                            .add(dropIndex, draggedItem);

                    event.setDropCompleted(true);

                    subjectPriorityList
                            .getSelectionModel()
                            .select(dropIndex);

                } else {

                    event.setDropCompleted(false);
                }

                event.consume();
            });

            cell.setOnDragEntered(e -> {

                cell.setStyle(
                        "-fx-background-color: #E8F0FE;" +
                                "-fx-background-radius: 14;" +
                                "-fx-padding: 6 0;"
                );
            });

            cell.setOnDragExited(e -> {

                cell.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-padding: 6 0;"
                );
            });

            return cell;
        });

        ListView<Room> selectedList = new ListView<>(selectedRooms);
        selectedList.getStyleClass().add("list-view");
        selectedList.setPrefWidth(250);
        selectedList.setPrefHeight(420);
        selectedList.setFocusTraversable(false);
        selectedList.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background-insets: 0;"
        );

        selectedList.setCellFactory(lv -> {

            ListCell<Room> cell = new ListCell<>() {

                private final VBox wrapper = new VBox();

                {

                    selectedProperty().addListener((obs, oldV, newV) -> {

                        if (newV) {

                            wrapper.setStyle(
                                    "-fx-background-color: #EFF6FF;" +
                                            "-fx-background-radius: 12;" +
                                            "-fx-border-color: #2563EB;" +
                                            "-fx-border-radius: 12;"
                            );

                        } else {

                            wrapper.setStyle(
                                    "-fx-background-color: white;" +
                                            "-fx-background-radius: 12;" +
                                            "-fx-border-color: #E5E7EB;" +
                                            "-fx-border-radius: 12;"
                            );
                        }
                    });
                }

                @Override
                protected void updateItem(Room room, boolean empty) {

                    super.updateItem(room, empty);

                    if (empty || room == null) {
                        setGraphic(null);
                    } else {
                        Label index = new Label((getIndex() + 1) + ".");
                        index.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");

                        Label name = new Label("Room " + room.getRoomNo());
                        name.setStyle(
                                "-fx-font-weight: bold;" +
                                        "-fx-text-fill: #111827;"
                        );

                        Label cap = new Label(
                                room.getRows() + " × " +
                                        room.getColumns() +
                                        " • Capacity: " +
                                        room.getCapacity()
                        );                        cap.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11;");

                        VBox textBox = new VBox(name, cap);
                        textBox.setSpacing(3);

                        HBox row = new HBox(10, index, textBox);

                        row.setAlignment(Pos.CENTER_LEFT);

                        wrapper.getChildren().setAll(row);

                        wrapper.setPadding(new Insets(12));

                        wrapper.setStyle(
                                "-fx-background-color: white;" +
                                        "-fx-background-radius: 12;" +
                                        "-fx-border-color: #E5E7EB;" +
                                        "-fx-border-radius: 12;"
                        );

                        wrapper.setOnMouseEntered(e -> {

                            wrapper.setStyle(
                                    "-fx-background-color: #FAFAFA;" +
                                            "-fx-background-radius: 12;" +
                                            "-fx-border-color: #CBD5E1;" +
                                            "-fx-border-radius: 12;"
                            );
                        });

                        wrapper.setOnMouseExited(e -> {

                            wrapper.setStyle(
                                    "-fx-background-color: white;" +
                                            "-fx-background-radius: 12;" +
                                            "-fx-border-color: #E5E7EB;" +
                                            "-fx-border-radius: 12;"
                            );
                        });

                        setGraphic(wrapper);

                        setStyle(
                                "-fx-background-color: transparent;" +
                                        "-fx-padding: 4 0;"
                        );
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

            cell.setOnDragEntered(e -> {

                cell.setStyle(
                        "-fx-background-color: #E8F0FE;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 4 0;"
                );
            });

            cell.setOnDragExited(e -> {

                cell.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-padding: 4 0;"
                );
            });

            cell.selectedProperty().addListener((obs, oldV, newV) -> {

                cell.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-padding: 4 0;"
                );
            });

            return cell;
        });


        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("primary-btn");
        removeBtn.setMaxWidth(Double.MAX_VALUE);

        removeBtn.setOnAction(e -> {
            Room selected = selectedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.selectedProperty().set(false);

                selectedRooms.remove(selected);

                table.refresh();

                selectedTitle.setText(
                        "Selected Rooms (Priority Wise): " +
                                selectedRooms.size()
                );

                int selectedCapacity = selectedRooms.stream()
                        .mapToInt(Room::getCapacity)
                        .sum();

                selectedCapacityLabel.setText(
                        selectedCapacity + " / " + requiredSeats[0]
                );

                if (selectedCapacity >= requiredSeats[0]) {

                    statusLabel.setText(" Enough Capacity");

                    statusLabel.setStyle(
                            "-fx-text-fill: green;" +
                                    "-fx-font-weight: bold;"
                    );

                } else {

                    statusLabel.setText(" Not Enough Capacity");

                    statusLabel.setStyle(
                            "-fx-text-fill: red;" +
                                    "-fx-font-weight: bold;"
                    );
                }
            }
        });

        Button generateBtn = new Button("Confirm Seating");
        generateBtn.getStyleClass().add("primary-btn");
        generateBtn.setMaxWidth(Double.MAX_VALUE);

        generateBtn.setOnAction(e -> {

            SharedData.prioritizedSubjects.setAll(
                    subjectPriorityList.getItems()
            );

            if (selectedRooms.isEmpty()) {

                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("No Room Selected");

                alert.setHeaderText("Selection Required");

                alert.setContentText(
                        "Please select at least one room before generating seating."
                );

                alert.showAndWait();

                return;
            }

            int totalCapacity = selectedRooms.stream()
                    .mapToInt(Room::getCapacity)
                    .sum();

            if (totalCapacity < requiredSeats[0]) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Insufficient Capacity");
                alert.setHeaderText("Not Enough Seats");

                alert.setContentText(
                        "Total Students: " + SharedData.totalStudents +
                                "\nRequired Seats: " + requiredSeats[0] +
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

        VBox actionButtons;

        VBox rightPanel = new VBox(10,
                selectedTitle,
                selectedList,
                actionButtons = new VBox(10, removeBtn, generateBtn)
        );
        actionButtons.setFillWidth(true);
        VBox.setVgrow(selectedList, Priority.ALWAYS);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(320);
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

                    if (selectedCapacity >= requiredSeats[0]) {
                        statusLabel.setText(" Enough Capacity");
                        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        statusLabel.setText(" Not Enough Capacity");
                        statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }

                    selectedCapacityLabel.setText(
                            selectedCapacity + " / " + requiredSeats[0]
                    );
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
        //table.setPrefWidth(700);
        HBox.setHgrow(table, Priority.ALWAYS);
        table.setPrefHeight(520);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(selectCol, roomCol, rowCol, colCol, capCol);

        table.setFixedCellSize(44);
        table.setStyle(
                "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: #E5E7EB;"
        );
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

        FlowPane infoBar = new FlowPane();
        infoBar.setHgap(20);
        infoBar.setVgap(20);
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
        scroll.setFitToHeight(true);
        scroll.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

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
                    setStyle(
                            "-fx-background-color: #E8F0FE;" +
                                    "-fx-table-cell-border-color: transparent;"
                    );
                } else {
                    setStyle(
                            "-fx-background-color: white;"
                    );
                }
            }

            {
                setOnMouseClicked(e -> {
                    if (!isEmpty()) {
                        Room selected = getItem();
                        System.out.println("Selected Room: " + selected.getRoomNo());
                    }
                });

                setOnMouseEntered(e -> {

                    if (!isEmpty() && !getItem().isSelected()) {

                        setStyle(
                                "-fx-background-color: #F8FAFC;"
                        );
                    }
                });

                setOnMouseExited(e -> {

                    if (!isEmpty() && !getItem().isSelected()) {

                        setStyle(
                                "-fx-background-color: white;"
                        );
                    }
                });

            }
        });


        VBox linearCard = createArrangementCard(
                linearBtn,
                "Linear Seating",
                "Students are seated continuously without empty gaps. Best for maximum seat utilization.",
                "A A A A\nB B B B",
                "Requires normal room capacity"
        );

        VBox strictCard = createArrangementCard(
                alternateStrictBtn,
                "Alternate Seating (Strict)",
                "Students from different subjects are seated alternately. Empty seats will be inserted if one subject has fewer students.",
                "A B A B\nA Null A Null",
                "Requires higher room capacity"
        );

        VBox balancedCard = createArrangementCard(
                alternateBalancedBtn,
                "Alternate Seating (Balanced)",
                "Students are seated alternately as much as possible. Remaining students are seated normally without empty gaps.",
                "A B A B\nA A A A",
                "Requires normal room capacity"
        );

        Label arrangementTitle = new Label("Arrangement Style");

        arrangementTitle.setStyle(
                "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        FlowPane arrangementBox = new FlowPane();

        arrangementBox.setHgap(20);
        arrangementBox.setVgap(20);

        arrangementBox.getChildren().addAll(
                linearCard,
                strictCard,
                balancedCard
        );

        VBox arrangementSection = new VBox(
                15,
                arrangementTitle,
                arrangementBox
        );

        HBox layout = new HBox(20, table, rightPanel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);

        VBox vBox = new VBox(20);
        Label footer = new Label(
                "Higher priority subjects are arranged first during seating generation."
        );

        footer.setStyle(
                "-fx-text-fill: #9CA3AF;" +
                        "-fx-font-size: 11;"
        );

        VBox subjectPriorityCard = new VBox(
                16,
                subjectPriorityTitle,
                subjectPriorityDesc,
                subjectPriorityList,
                footer
        );

        subjectPriorityCard.setPadding(new Insets(20));

        subjectPriorityCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;"
        );

        vBox.getChildren().addAll(
                infoBar,
                arrangementSection,
                subjectPriorityCard,
                layout
        );
        vBox.setPadding(new Insets(20));
        VBox.setMargin(actionButtons, new Insets(10, 0, 0, 0));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );
        return scrollPane;
    }

    private static int calculateRequiredSeats() {

        try {

            PreparedStatement ps =
                    DB_Methods.con.prepareStatement(
                            "SELECT COUNT(*) as total " +
                                    "FROM rawdata " +
                                    "GROUP BY Sub_code " +
                                    "ORDER BY total DESC " +
                                    "LIMIT 1"
                    );

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int maxSubjectStudents =
                        rs.getInt("total");

                return maxSubjectStudents * 2;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

    private static VBox createInfoCard(String title, Label valueLabel) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setMinWidth(180);
        card.setPrefWidth(180);
        card.setMaxWidth(220);

        card.getStyleClass().add("card");

        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");

        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(160);
        valueLabel.setStyle("-fx-font-size: 20;"+
                "-fx-font-weight: bold;"+
                "-fx-text-fill: #111827");
        //valueLabel.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(t, valueLabel);

        return card;
    }

    private static VBox createArrangementCard(
            RadioButton radio,
            String title,
            String description,
            String example,
            String capacityNote
    ) {

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );

        Label descLabel = new Label(description);

        descLabel.setWrapText(true);

        descLabel.setStyle(
                "-fx-text-fill: #4B5563;" +
                        "-fx-font-size: 12;"
        );

        Label exampleLabel = new Label(example);

        exampleLabel.setStyle(
                "-fx-font-family: 'Consolas';" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2563EB;"
        );

        Label capacityLabel = new Label(capacityNote);

        capacityLabel.setStyle(
                "-fx-text-fill: #059669;" +
                        "-fx-font-size: 11;" +
                        "-fx-font-weight: bold;"
        );

        VBox card = new VBox(
                12,
                radio,
                titleLabel,
                descLabel,
                exampleLabel,
                capacityLabel
        );

        card.setPadding(new Insets(18));

        card.setPrefWidth(240);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #D1D5DB;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;"
        );

        card.setCursor(Cursor.HAND);

        Runnable updateCardStyle = () -> {

            if (radio.isSelected()) {

                card.setStyle(
                        "-fx-background-color: #F8FBFF;" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: #2563EB;" +
                                "-fx-border-radius: 14;" +
                                "-fx-border-width: 2;" +
                                "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.15), 12, 0, 0, 4);"
                );

                card.setScaleX(1.02);
                card.setScaleY(1.02);

            } else {

                card.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: #D1D5DB;" +
                                "-fx-border-radius: 14;" +
                                "-fx-border-width: 1;"
                );

                card.setScaleX(1);
                card.setScaleY(1);
            }
        };

        radio.selectedProperty().addListener((obs, oldV, newV) -> {
            updateCardStyle.run();
        });

        radio.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        radio.setStyle(
                "-fx-padding: 0 0 8 0;"
        );

        updateCardStyle.run();

        card.setOnMouseClicked(e -> {

            radio.setSelected(true);

            radio.fire();

        });

        card.setOnMouseEntered(e -> {

            if (!radio.isSelected()) {

                card.setStyle(
                        "-fx-background-color: #FAFAFA;" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: #CBD5E1;" +
                                "-fx-border-radius: 14;" +
                                "-fx-border-width: 1;"
                );
            }
        });

        card.setOnMouseExited(e -> {

            updateCardStyle.run();
        });

        return card;
    }

}
