package com.planner.GUI.Screens;

import com.planner.GUI.HomePage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.InputStream;

public class AboutScreen {


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

        InputStream is = AboutScreen.class.getResourceAsStream("/E-SAPlogo.jpg");

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


