package com.planner.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

public class CardComponent {
    public static StackPane createCard(String titleText, String descText, String imagePath, Runnable action) {

        StackPane card = new StackPane();
        card.setPrefSize(300, 400);
        card.setMaxSize(300, 400);
        card.getStyleClass().add("card");

        VBox content = new VBox(50);
        content.setPadding(new Insets(15));
        content.setAlignment(Pos.CENTER);

        InputStream stream = CardComponent.class.getResourceAsStream(imagePath);
        ImageView image;
        if (stream != null) {
            image = new ImageView(new Image(stream));
        } else {
            System.out.println("Image not found: " + imagePath);
            image = new ImageView(); // empty fallback
        }

        image.setFitWidth(100);
        image.setFitHeight(100);

        Label title = new Label(titleText);
        title.getStyleClass().add("title");

        Label subtitle = new Label(descText);
        subtitle.getStyleClass().add("subtitle");

        Button btn = new Button("Open");
        btn.getStyleClass().add("button-primary");
        btn.setPrefHeight(50);
        btn.setPrefWidth(150);

        btn.setOnAction(e -> action.run());

        content.getChildren().addAll(image, title, subtitle, btn);
        card.getChildren().addAll(content);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.05);
            card.setScaleY(1.05);
            card.setTranslateY(-5);
            card.setCursor(Cursor.HAND);
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
            card.setTranslateY(0);
        });

        return card;
    }


}
