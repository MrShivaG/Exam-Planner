package com.planner.GUI;

import com.planner.Database.DB_Methods;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UpdateRoom extends Application {
    DB_Methods dbMethods = new DB_Methods();

    public UpdateRoom() throws SQLException {
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage = editroom(104);
        stage.show();
    }
    public Stage editroom(int roomno) throws SQLException {
        BorderPane editpane = new BorderPane();
        VBox headerbox = new VBox();
        headerbox.setPadding(new Insets(20));
        Label headerlabel = new Label("Update Room "+roomno);
        headerlabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: black; -fx-pref-height: 3px;");

        headerbox.getChildren().addAll(headerlabel,sep);


        GridPane contentPane = new GridPane();
        contentPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 6 10 6 10;" +
                        "-fx-font-size: 13px;"
        );
        contentPane.setVgap(20);
        contentPane.setHgap(20);
        contentPane.setAlignment(Pos.CENTER);

        int[] arr = dbMethods.fetchRowColumn(roomno);
        int temp = arr[0];
        arr[0] = arr[1];
        arr[1] = temp;


        Label rowlable = new Label("Rows");
        rowlable.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        TextField rowtext = new TextField();
        rowtext.setText(String.valueOf(arr[0]));
        rowtext.setPrefSize(200, 30);
        rowtext.setPromptText("Enter row no...");

        Label columnlable = new Label("Columns");
        columnlable.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        TextField columntext = new TextField();
        columntext.setText(String.valueOf(arr[1]));
        columntext.setPrefSize(200, 30);
        columntext.setPromptText("Enter column no...");

        Label notificationlable = new Label();
        notificationlable.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;  -fx-font-style: italic;");

        HBox bottombox = new HBox(notificationlable);
        bottombox.setAlignment(Pos.CENTER);
        bottombox.setStyle(" -fx-padding: 10px;");
        bottombox.setVisible(false);



        Button editbutton = new Button("Update");
        editbutton.setStyle(
                "-fx-background-color: #185FA5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 24 8 24;" +
                        "-fx-cursor: hand;"
        );
        editbutton.setOnAction(e -> {
            editpane.setBottom(bottombox);
            bottombox.setVisible(true);
            HomePage homepageobj = new HomePage();
            try {
                RoomScreen.room(homepageobj);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                int row = Integer.parseInt(rowtext.getText());
                int column = Integer.parseInt(columntext.getText());
                dbMethods.updatedata(roomno, row, column);


                notificationlable.setText("Room updated Successfully!");
            } catch (NumberFormatException ex) {
                notificationlable.setText("Error: Please enter valid numbers.");
            } catch (SQLException ex) {
                notificationlable.setText("Error: DB issue.");
            }
        });

        contentPane.add(rowlable,0,1,1,1);
        contentPane.add(columnlable,0,2,1,1);


        contentPane.add(rowtext,1,1,1,1);
        contentPane.add(columntext,1,2,1,1);
        contentPane.add(editbutton,1,3,1,1);

        editpane.setTop(headerbox);
        editpane.setCenter(contentPane);
        Stage stage = new Stage();
        Scene scene = new Scene(editpane,400,400);
        stage.setScene(scene);
        return stage;
    }
}
