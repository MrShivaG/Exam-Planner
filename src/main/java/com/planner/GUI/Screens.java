package com.planner.GUI;

import com.planner.Database.DB_Methods;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TableView;

import static com.planner.Database.ArrangementsDB.fetcharrData;

public class Screens {

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
        next.setOnAction(e -> {
                    app.switchCenter(new Pane());
                }
                );
        next.getStyleClass().add("button-primary");

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

    public static VBox createRoomScreen(HomePage app) {

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Room Management");

        Button back = new Button("← Back");
        back.setOnAction(e -> app.switchCenter(new Pane()));

        layout.getChildren().addAll(title, back);

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
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("File Selected Successfully");
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
                    HBox card = databox(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
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
    public static HBox databox(String arr_table_name, String arr_name, String date, String capacity, String session, String status, String student){
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

        Label timelable = new Label("            10:00 - 01:00");
        timelable.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #6B7280;"
        );
        dateVbox.getChildren().addAll(datebox,timelable);
        //date and time end

        String examStatus = getExamStatus(date);

        Label statuslable = new Label();
        statuslable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;" +
                        "-fx-font-style: italic;"
        );
        if (examStatus.equals("Today")) {
            statuslable.setText("HAPPENING TODAY");
            statuslable.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        } else {
            statuslable.setText("Status: " + examStatus);
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
        arrrowBox.getChildren().addAll(roomnobox,dateVbox,spacer,statuslable,capacitybox,arrButton);

        return arrrowBox;
    }

    public static String getExamStatus(String dateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");


        LocalDate examDate = LocalDate.parse(dateStr, formatter);
        LocalDate today = LocalDate.now();

        // 3. Compare karein
        if (examDate.equals(today)) {
            return "Today";
        } else if (examDate.equals(today.plusDays(1))) {
            return "Tomorrow";
        } else if (examDate.isBefore(today)) {
            return "Completed";
        } else {

            long daysLeft = ChronoUnit.DAYS.between(today, examDate);
            return "In " + daysLeft + " days";
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
}
