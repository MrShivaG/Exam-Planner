package com.planner.GUI;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;

public class Screens {

    // 🔷 ARRANGEMENT SCREEN
    public static BorderPane arrangementContent(HomePage app) {

        BorderPane layout = new BorderPane();

        Button button = new Button("+ New Arrangement");
        button.getStyleClass().add("button-primary");

        button.setOnAction(e -> app.switchCenter(new Pane()));

        HBox topBox = new HBox(button);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(20));

        layout.setTop(topBox);

        StackPane card = CardComponent.createCard(
                "Data Input",
                "Enter student data",
                "/input.png",
                () -> app.switchCenter(dataScreen(app))
        );

        layout.setCenter(new StackPane(card));

        return layout;
    }

    // 🔷 DATA SCREEN
    public static BorderPane dataScreen(HomePage app) {

        BorderPane layout = new BorderPane();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        StackPane card = CardComponent.createCard(
                "Upload File",
                "Drag & Drop your File",
                "/upload.png",
                () -> openFileChooser(app)
        );

        VBox center = new VBox(20);
        center.setAlignment(Pos.CENTER);

        Label title = new Label("Data Input Screen");

        Button back = new Button("← Back");
        back.setOnAction(e -> app.switchCenter(arrangementContent(app)));

        center.getChildren().addAll(title, back);

        grid.add(card, 0, 0);
        grid.add(center, 0, 1);

        layout.setCenter(grid);

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
}