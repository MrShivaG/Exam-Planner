package com.planner.GUI.Screens;

import com.planner.Database.ArrangementsDB;
import com.planner.Database.DB_Methods;
import com.planner.GUI.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javax.sound.midi.ShortMessage;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.planner.GUI.HomePage.createTopBar;

public class DashboardComponents {
    private static HomePage app = new HomePage();

    public DashboardComponents(HomePage app) {
        this.app = app;
    }

    public static HBox databox(String arr_group_name, String date, int capacity, String session, int student){
        //room no. box
        VBox sessionbox = new VBox();
        sessionbox.setPadding(new Insets(5,5,5,5));
        sessionbox.getStyleClass().add("cardrow");
        sessionbox.setAlignment(Pos.CENTER);
        sessionbox.setStyle(
                "-fx-background-color: WHITE;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e0e0ec;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );
        Label sessionlable = new Label("Session");
        sessionlable.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #6B7280;"
        );

        Label sessiondata = new Label(session);
        sessiondata.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        sessionbox.getChildren().addAll(sessionlable,sessiondata);

        VBox groupnamebox = new VBox();
        String[] parts = arr_group_name.split("_");
        String groupname = parts[0];
        String sem = parts[1];

        Label gnamelable = new Label(groupname);
        gnamelable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: 650;"
        );

        HBox sembox = new HBox();
        Label semlable = new Label("SEM - ");
        semlable.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-text-fill: #6B7280;"
        );
        Label semdata = new Label(sem);
        semdata.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-text-fill: #6B7280;"
        );

        sembox.setPadding(new Insets(2, 0, 2, 0));
        sembox.getChildren().addAll(semlable,semdata);
        groupnamebox.getChildren().addAll(gnamelable,sembox);

        //Date and time
        VBox dateVbox = new VBox();

        HBox datebox = new HBox();
        Label datelable = new Label("DATE - ");
        datelable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 15px;"
        );
        Label datedata = new Label(""+date);
        datedata.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );
        datebox.setAlignment(Pos.CENTER);
        datebox.setPadding(new Insets(2, 0, 2, 0));
        datebox.getChildren().addAll(datelable,datedata);

        String time = "10:00 - 01:00";
        Label timelable = new Label(" TIME - 10:00 - 01:00");
        timelable.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #6B7280;"
        );

        dateVbox.getChildren().addAll(datebox,timelable);
        //date and time end

        String examStatus = getExamStatus(date,time);
        HBox statusbox = new HBox();
        statusbox.setAlignment(Pos.CENTER);
        Label status = new Label("Status - ");
        status.setStyle("-fx-text-fill: #1a1a2e;" +
                "-fx-font-size: 18px;");

        Label statuslable = new Label();
        statuslable.setStyle(
                "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-style: italic;"
        );
        if (examStatus.equals("Today")) {
            statuslable.setText("HAPPENING TODAY");
            statuslable.setStyle("-fx-text-fill: #EF4444;" +
                    "-fx-font-size: 14px;"+
                    " -fx-font-weight: bold;" +
                    "-fx-font-style: italic;");
            statusbox.getChildren().addAll(status,statuslable);
        } else if (examStatus.equals("Completed")) {
            statuslable.setText("COMPLETED");
            statuslable.setStyle("-fx-text-fill: #10B981;" +
                    "-fx-font-size: 18px;"+
                    " -fx-font-weight: bold;" +
                    "-fx-font-style: italic;");
            statusbox.getChildren().addAll(status,statuslable);

        } else {
            statuslable.setText(examStatus);
            statusbox.getChildren().addAll(status,statuslable);
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
                        "-fx-font-weight: bold;"
        );
        capacitybox.getChildren().addAll(capacitylable,capacitydata);
        //capacity end

        Button arrButton = new Button(">");
        arrButton.setStyle("-fx-font-size: 18px;" +
                "-fx-font-weight: bold;");

        arrButton.setOnAction(e ->{

        });


        Button deletearr = new Button();
        Image deleteImg = new Image(DashboardComponents.class.getResourceAsStream("/delete.png"));

        ImageView deleteIcon = new ImageView(deleteImg);
        deleteIcon.setFitHeight(28);
        deleteIcon.setFitWidth(20);

        deletearr.setGraphic(deleteIcon);
        deletearr.getStyleClass().add("deletearrbutton");

        deletearr.setOnAction(e->{
            try {
                DB_Methods dbMethods = new DB_Methods();

                boolean isConfirmed = Notification.confirm("Are you sure you want to delete the arrangement of ");
                if (isConfirmed) {
                    try {
                        dbMethods.deleteArrRoom(arr_group_name);
                        Notification.message("Arrangement of Room no successfully deleted.");
                        DashboardScreen dashboardScreen = new DashboardScreen();
                        app.switchScreen(createTopBar("Dashboard"), DashboardScreen.dashboardContent(app));



                    } catch (SQLException ex) {
                        Notification.message("An error occurred while deleting." + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });



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
        arrrowBox.getChildren().addAll(sessionbox,groupnamebox,dateVbox,spacer,statusbox,capacitybox,arrButton,deletearr);

        return arrrowBox;
    }

    public static VBox createStatCard(String title, String value, String bottomLeft, String bottomRight) {

        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(180);

        card.getStyleClass().add("card");

        Label label = new Label(title);
        label.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 14;");

        Label number = new Label(value);
        number.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

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

    public static VBox createDatabaseStatusCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(250);
        card.setPrefHeight(180);
        card.getStyleClass().add("card");

        boolean isConnected = false;

        try (Connection c = ArrangementsDB.connection()) {
            isConnected = true;
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

        Circle dot = new Circle(5);
        dot.setFill(Color.web(statusColor));

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

    public static String getExamStatus(String dateStr, String timeRange) {

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        try {
            LocalDate examDate = DateUtil.parse(dateStr);
            LocalDate today = LocalDate.now();

            String startTimeStr = timeRange.split("-")[0].trim();

            LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime now = LocalTime.now();

            if (examDate.isBefore(today)) {
                return "Completed";
            }
            else if (examDate.equals(today)) {

                if (now.isAfter(startTime)) {
                    return "Completed";
                } else {
                    return "Today";
                }
            }
            else if (examDate.equals(today.plusDays(1))) {
                return "Tomorrow";
            }
            else {
                long daysLeft = ChronoUnit.DAYS.between(today, examDate);
                return "In " + daysLeft + " days";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Format";
        }
    }

}
