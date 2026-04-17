package com.planner.GUI;

import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;

public class Screens {

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
}
