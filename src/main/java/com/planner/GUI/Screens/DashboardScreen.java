package com.planner.GUI.Screens;

import com.planner.Database.DB_Methods;
import com.planner.GUI.HomePage;
import static com.planner.GUI.Screens.DashboardComponents.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardScreen {

    public static Node dashboardContent(HomePage app) {

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_LEFT);

        int totalRoom = 0;
        int capacity = 0;
        boolean isConnected = false;

        DB_Methods dbMethods = null;

        try {

            dbMethods = new DB_Methods();

            if (DB_Methods.con != null) {

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
                List<String[]> arrangement = dbMethods.fetch_groups_names();

                for (String[] data : arrangement) {
                    List<String[]> arr_data = dbMethods.fetch_group_tables(data[0]);
                    int totalCapacity = 0;
                    int totalStudents = 0;

                    for (String[] arrdata : arr_data) {
                        if (arrdata[3] != null) totalCapacity += Integer.parseInt(arrdata[3]);
                        if (arrdata[5] != null) totalStudents += Integer.parseInt(arrdata[5]);
                    }

                    String date = arr_data.get(0)[2];
                    String session = arr_data.get(0)[4];

                    HBox card = databox(app, data[0], date, totalCapacity, session, totalStudents);
                    cardsContainer.getChildren().add(card);
                }

            } catch (Exception e) {
                e.printStackTrace();
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

}
