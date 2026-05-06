package com.planner.GUI;

import com.planner.Database.DB_Methods;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;


public class RoomScreen extends Application {
    static int totalrooms;
    static int totalstudentcapacity;
    static DB_Methods dbMethods;

    static {
        try {
            dbMethods = new DB_Methods();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RoomScreen() throws SQLException {
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene mainscene = new Scene(room(null), 1200, 700);
        URL css = getClass().getResource("/app.css");
        if (css != null) {
            mainscene.getStylesheets().add(css.toExternalForm());
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("CSS File Not Found");
            alert.showAndWait();
        }
        stage.setScene(mainscene);
        stage.setX(0);
        stage.setY(0);
        stage.show();
    }

    public static BorderPane room(HomePage app) throws SQLException {
        BorderPane borderPane = new BorderPane();
        ScrollPane scrollpane = new ScrollPane();
        scrollpane.setFitToWidth(true);
        scrollpane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;" +
                        "-fx-border-color: transparent;"
        );


        VBox contentbox = new VBox(20);
        contentbox.setPadding(new Insets(10, 32, 32, 32));
        contentbox.setStyle("-fx-background-color: #F8F9FA;");

        HBox hBox = new HBox(6);
        hBox.setAlignment(Pos.CENTER_LEFT);


        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        header.setSpacing(20);



        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10,0,0,0));
        Label title = new Label("Room & Capacity Management");
        title.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );
        Label subtitle = new Label("Manage room assets and seating limits.");
        subtitle.setStyle(
                "-fx-text-fill: #6B7280; -fx-font-size: 13;" +
                        "-fx-font-style: italic;"
        );





        titleBox.getChildren().addAll(title,subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Button addRoomBtn = new Button("+ Add Class Room");
        addRoomBtn.setStyle(
                "-fx-background-color:#1a56db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );

        addRoomBtn.setOnMouseEntered(e -> addRoomBtn.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        ));

        addRoomBtn.setOnAction(e -> {
            AddNewRoom newroom = new AddNewRoom();
            Stage stage = newroom.newroom();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addRoomBtn.getScene().getWindow());
            stage.showAndWait();
        });





        VBox capacityCard = capacity();
        capacityCard.setPrefSize(200,80);
        VBox techCard = totalRoomCount();
        techCard.setPrefSize(200,80);



        header.getChildren().addAll(titleBox,spacer, techCard,capacityCard,spacer1, addRoomBtn);

        VBox cardBox = new VBox();
        cardBox.setPadding(new Insets(15));
        cardBox.setPrefSize(500, 400);
        cardBox.setMaxWidth(Double.MAX_VALUE);
        cardBox.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #c0c8d0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        FlowPane roomcardrow = new FlowPane();
        roomcardrow.setHgap(20);
        roomcardrow.setVgap(20);
        roomcardrow.setAlignment(Pos.CENTER);

        List<int[]> rooms = dbMethods.fetchRowColumn();
        for (int i = 0; i < rooms.size(); i++) {
            int[] data = rooms.get(i);
            VBox card = roomcard(data[0], data[1], data[2]);
            HBox.setHgrow(card, Priority.ALWAYS);
            roomcardrow.getChildren().add(card);
        }




        cardBox.getChildren().addAll(roomcardrow);
        contentbox.getChildren().addAll(hBox,header,cardBox);
        scrollpane.setContent(contentbox);
        borderPane.setCenter(scrollpane);


        return  borderPane;
    }
    private static VBox capacity() throws SQLException {
        VBox card = new VBox(6);
        card.getStyleClass().add("roomcard");
        card.setPadding(new Insets(10, 20, 10, 20));
        card.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label tagLabel = new Label("LIVE CAPACITY");
        tagLabel.setStyle(
                "-fx-text-fill: #6B7280; -fx-font-size: 14;"
        );

        totalstudentcapacity =dbMethods.totalcapacity();

        Label numLabel = new Label(String.valueOf(totalstudentcapacity));
        numLabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                "-fx-font-size: 28; -fx-font-weight: bold;"
        );

        Label subLabel = new Label("Total Load Seats");
        subLabel.setStyle(
                "-fx-text-fill: #6B7280; -fx-font-size: 11;"
        );

        Region spacer = new Region();
        spacer.setPrefHeight(8);



        card.getChildren().addAll(tagLabel, numLabel, subLabel, spacer);
        return card;
    }
    private static VBox totalRoomCount() throws SQLException {
        VBox card = new VBox(6);
        card.getStyleClass().add("roomcard");
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label tagLabel = new Label("TOTAL ROOMS");
        tagLabel.setStyle(
                "-fx-text-fill: #7a7a9a;" +"-fx-text-fill: #6B7280; -fx-font-size: 14;"
        );

        totalrooms =dbMethods.totalroom();
        Label numLabel = new Label(String.valueOf(totalrooms));
        numLabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        spacer.setPrefHeight(8);

        card.getChildren().addAll(tagLabel, numLabel, spacer);
        return card;
    }
    private static VBox roomcard(int roomNo, int rows, int columns) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        card.getStyleClass().add("roomcard");


        VBox body = new VBox(8);
        body.setPadding(new Insets(14, 16, 14, 16));

        Label roomnolabel = new Label("Room No: " + roomNo);
        roomnolabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        Region seperator = new Region();

        seperator.setStyle("-fx-background-color: #e6e6ed;");
        seperator.setMaxWidth(Double.MAX_VALUE);

        Label locationLabel = new Label("ADMIN CENTER - BLOCK A");
        locationLabel.setStyle(
                "-fx-text-fill: #7a7a9a;" +
                        "-fx-font-size: 12px;"+
                        "-fx-font-weight: bold;"
        );

        VBox detailsRow = new VBox();
        detailsRow.setAlignment(Pos.CENTER_LEFT);

        Label rowlabel = new Label("Rows: " + rows);
        rowlabel.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 18px;");

        Label columnlabel = new Label("Columns: " + columns);
        columnlabel.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 18px;");

        Label capacitylabel = new Label("Capacity: " + (rows * columns));
        capacitylabel.setStyle("-fx-text-fill: #7a7a9a; -fx-font-size: 18px;");

        detailsRow.getChildren().addAll(rowlabel, columnlabel, capacitylabel);

        Region sep = new Region();
        sep.setPrefHeight(1);

        sep.setStyle("-fx-background-color: #e0e0ec;");
        sep.setMaxWidth(Double.MAX_VALUE);

        HBox bottomRow = new HBox(8);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(4, 0, 0, 0));

        Button editBtn = new Button("EDIT DETAILS");
        editBtn.getStyleClass().add("editbutton");


        editBtn.setOnAction(e->{
            try {
                UpdateRoom update = new UpdateRoom();
                Stage stage = update.editroom(roomNo);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(editBtn.getScene().getWindow());
                stage.showAndWait();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        Button dashBtn = new Button("\uD83D\uDDD1");
        dashBtn.getStyleClass().add("deletebutton");
        dashBtn.setOnAction(e -> {

            boolean isConfirmed = Notification.confirm("Are you sure you want to delete " + roomNo);


            if (isConfirmed) {
                try {
                    dbMethods.deleteRoom(roomNo);
                    Notification.message("Room " + roomNo + " successfully deleted.");

                } catch (SQLException ex) {
                    Notification.message("An error occurred while deleting." + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        bottomRow.getChildren().addAll(editBtn, dashBtn);
        body.getChildren().addAll(roomnolabel, seperator,locationLabel, detailsRow, sep, bottomRow);
        card.getChildren().addAll( body);

        return card;
    }

}
