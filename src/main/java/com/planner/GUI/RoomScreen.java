package com.planner.GUI;

import com.planner.Database.DB_Methods;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        contentbox.setPadding(new Insets(24, 32, 32, 32));
        contentbox.setStyle("-fx-background-color: #e4ecf0;");

        HBox hBox = new HBox(6);
        hBox.setAlignment(Pos.CENTER_LEFT);


        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        header.setPadding(new Insets(10));

        Region sepe = new Region();
        sepe.setPrefHeight(1);
        sepe.setStyle("-fx-background-color: BLACK;");
        sepe.setMaxWidth(Double.MAX_VALUE);

        VBox titleBox = new VBox(4);
        Label title = new Label("Room & Capacity Management");
        Line underline = new Line(5,70,350,70);
        title.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );


        titleBox.getChildren().addAll(title,underline);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

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
        header.getChildren().addAll(titleBox, spacer, addRoomBtn);


        HBox row = new HBox(16);

        VBox capacityCard = capacity();
        capacityCard.setPrefSize(400,150);
        VBox techCard = totalRoomCount();
        techCard.setPrefSize(400,150);


        row.setAlignment(Pos.CENTER);

        row.getChildren().addAll(techCard,capacityCard);


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





        contentbox.getChildren().addAll(hBox,header,sepe,row,roomcardrow);
        scrollpane.setContent(contentbox);
        borderPane.setCenter(scrollpane);


        return  borderPane;
    }
    private static VBox capacity() throws SQLException {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label tagLabel = new Label("LIVE CAPACITY");
        tagLabel.setStyle(
                "-fx-text-fill: #7a7a9a;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
        );

        totalstudentcapacity =dbMethods.totalcapacity();

        Label numLabel = new Label(String.valueOf(totalstudentcapacity));
        numLabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;"
        );

        Label subLabel = new Label("Total Load Seats");
        subLabel.setStyle(
                "-fx-text-fill: #7a7a9a;" +
                        "-fx-font-size: 11px;"
        );

        Region spacer = new Region();
        spacer.setPrefHeight(8);



        card.getChildren().addAll(tagLabel, numLabel, subLabel, spacer);
        return card;
    }
    private static VBox totalRoomCount() throws SQLException {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        Label tagLabel = new Label("Total Room");
        tagLabel.setStyle(
                "-fx-text-fill: #7a7a9a;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
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

//        StackPane imgBox = new StackPane();
//        imgBox.setPrefHeight(130);
//        imgBox.setStyle(
//                "-fx-background-color: #2a2a3e;" +
//                        "-fx-background-radius: 10 10 0 0;"
//        );

        Label roomIcon = new Label();
        roomIcon.setStyle("-fx-font-size: 36px;");
//        imgBox.getChildren().add(roomIcon);

        VBox body = new VBox(8);
        body.setPadding(new Insets(14, 16, 14, 16));

        Label roomnolabel = new Label("Room No: " + roomNo);
        roomnolabel.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        Region seperator = new Region();
        seperator.setPrefHeight(1);
        seperator.setStyle("-fx-background-color: #e0e0ec;");
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
        editBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #1a56db;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 4;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        );
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

        Button dashBtn = new Button("—");
        dashBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #7a7a9a;" +
                        "-fx-font-size: 12px;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 4;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5 10 5 10;" +
                        "-fx-cursor: hand;"
        );

        bottomRow.getChildren().addAll(editBtn, dashBtn);
        body.getChildren().addAll(roomnolabel, seperator,locationLabel, detailsRow, sep, bottomRow);
        card.getChildren().addAll( body);

        return card;
    }
}
