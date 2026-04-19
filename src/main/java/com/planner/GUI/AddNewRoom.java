package com.planner.GUI;

import com.planner.Database.DB_Methods;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;

public class AddNewRoom extends Application {
    DB_Methods dbMethods ;
    @Override
    public void start(Stage stage) throws Exception {
        stage = newroom();
        stage.show();

    }
    public Stage newroom(){
        BorderPane roompane = new BorderPane();
        VBox headerbox = new VBox();
        headerbox.setPadding(new Insets(20));
        headerbox.setStyle("-fx-background-color: white;");
        Label headerlabel = new Label("Add New Room");
        headerlabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );


        headerbox.getChildren().addAll(headerlabel);

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

        Label roomnolable = new Label("Room No.");
        roomnolable.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        TextField roomnotext = new TextField();
        roomnotext.setPrefSize(200, 30);
        roomnotext.setPromptText("Enter Room no...");

        Label rowlable = new Label("Rows");
        rowlable.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        TextField rowtext = new TextField();
        rowtext.setPrefSize(200, 30);
        rowtext.setPromptText("Enter row no...");

        Label columnlable = new Label("Columns");
        columnlable.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #111827;"
        );
        TextField columntext = new TextField();
        columntext.setPrefSize(200, 30);
        columntext.setPromptText("Enter column no...");

        Label notificationlable = new Label();
        notificationlable.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;  -fx-font-style: italic;");

        HBox bottombox = new HBox(notificationlable);
        bottombox.setAlignment(Pos.CENTER);
        bottombox.setStyle(" -fx-padding: 10px;");
        bottombox.setVisible(false);



        Button addbutton = new Button("Add");
        addbutton.setStyle(
                "-fx-background-color: #185FA5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 24 8 24;" +
                        "-fx-cursor: hand;"
        );
        addbutton.setOnAction(e->{
            roompane.setBottom(bottombox);
            bottombox.setVisible(true);


            try {
                int roomno = Integer.parseInt(roomnotext.getText());
                int rowno = Integer.parseInt(rowtext.getText());
                int columnno = Integer.parseInt(columntext.getText());
                dbMethods = new DB_Methods();
                dbMethods.insertData(roomno, rowno, columnno, true);
                notificationlable.setText("Room Added Successfully!");
            } catch (NumberFormatException ev) {
                notificationlable.setText("Error: Please enter valid numbers.");
            } catch (SQLException ev) {
                notificationlable.setText("Error: Room already exists .");
            }catch(Exception ev){

            }

            roomnotext.setText("");
            rowtext.setText("");
            columntext.setText("");
            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(ev -> bottombox.setVisible(false));

            pause.play();
        });


        contentPane.add(roomnolable,0,0,1,1);
        contentPane.add(rowlable,0,1,1,1);
        contentPane.add(columnlable,0,2,1,1);

        contentPane.add(roomnotext,1,0,1,1);
        contentPane.add(rowtext,1,1,1,1);
        contentPane.add(columntext,1,2,1,1);
        contentPane.add(addbutton,1,3,1,1);

        roompane.setTop(headerbox);
        roompane.setCenter(contentPane);
        Stage stage = new Stage();
        Scene scene = new Scene(roompane,400,400);
        stage.setScene(scene);
        return stage;
    }
}
