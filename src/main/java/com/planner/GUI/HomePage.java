package com.planner.GUI;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.sql.SQLException;

public class HomePage extends Application {

    private BorderPane root;
    private BorderPane rightSide;
    private Button activeButton = null;

    @Override
    public void start(Stage stage) {

        root = new BorderPane();

        Parent content = createDashboardScreen();

        StackPane mainStack = new StackPane(content);

        Scene scene = new Scene(mainStack, 1000, 650);
        URL css = getClass().getResource("/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setContentText("CSS File Not Found");
            alert.showAndWait();
        }

        stage.setScene(scene);
        stage.setTitle("Seating Planner");
        stage.setMaximized(true);
        stage.show();
        showWelcomeMessage(mainStack);
    }

    private void showWelcomeMessage(StackPane rootPane) {

        Label msg = new Label("Welcome back, Admin");
        msg.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: gray; " +
                        "-fx-padding: 30 60; " +
                        "-fx-background-radius: 20;"
        );

        msg.setOpacity(0);
        msg.getStyleClass().add("dashboard-title");

        rootPane.getChildren().add(msg);
        StackPane.setAlignment(msg, Pos.TOP_CENTER);
        StackPane.setMargin(msg, new Insets(50));

        FadeTransition in = new FadeTransition(Duration.seconds(0.8), msg);
        in.setFromValue(0);
        in.setToValue(1);

        FadeTransition out = new FadeTransition(Duration.seconds(0.8), msg);
        out.setFromValue(1);
        out.setToValue(0);
        out.setDelay(Duration.seconds(5));

        in.setOnFinished(e -> out.play());
        out.setOnFinished(e -> rootPane.getChildren().remove(msg));

        in.play();
    }

    private BorderPane createDashboardScreen() {

        BorderPane rootLayout = new BorderPane();

        HBox mainLayout = new HBox();

        VBox sidebar = createSidebar();

        rightSide = new BorderPane();
        rightSide.setTop(createHeader());
     //   rightSide.setBottom(createDeveloperSection());

        HBox.setHgrow(rightSide, Priority.ALWAYS);
        mainLayout.getChildren().addAll(sidebar, rightSide);

        rootLayout.setCenter(mainLayout);

        return rootLayout;
    }
    private BorderPane createHeader() {

        BorderPane header = new BorderPane();
        header.setPadding(new Insets(10, 20, 10, 20));
        //header.getStyleClass().add("glass");

        VBox centerBox = new VBox(2);
        centerBox.setAlignment(Pos.CENTER);

        Label heading = new Label("Manage your exam seating efficiently");
        heading.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Smart Exam Arrangement System");
        subtitle.getStyleClass().add("hyyuser");

        centerBox.getChildren().addAll(heading, subtitle);
        header.setCenter(centerBox);

        Label username = new Label("Admin");
        username.setStyle("-fx-text-fill: #111827; -fx-font-weight: bold;");
        header.setRight(username);

        return header;
    }

    private SVGPath getDashboardIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M3 13h8V3H3v10zm10 8h8V3h-8v18zM3 21h8v-6H3v6zm10 0h8v-6h-8v6z"
        );

        icon.setFill(Color.GRAY);
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getArrangementIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M3 6h18v2H3V6zm0 5h18v2H3v-2zm0 5h18v2H3v-2z"
        );

        icon.setFill(Color.GRAY);
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getClassroomIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M12 3L1 9l11 6 9-4.91V17h2V9L12 3zm0 13L3.74 10 12 5l8.26 5L12 16z"
        );

        icon.setFill(Color.GRAY);
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getLogoutIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M16 13v-2H7V8l-5 4 5 4v-3h9zm3-10H9a2 2 0 00-2 2v3h2V5h10v14H9v-3H7v3a2 2 0 002 2h10a2 2 0 002-2V5a2 2 0 00-2-2z"
        );

        icon.setFill(Color.GRAY);
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private VBox createSidebar() {

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("glass");

        // 🔷 Buttons
        Button dashboard = createSidebarButton("Dashboard", getDashboardIcon(),
                () -> switchCenter(new Pane()));
        Button arrangement = createSidebarButton(
                "Arrangements",
                getArrangementIcon(),
                () -> switchCenter(Screens.arrangementContent(this))
        );

        Button room = createSidebarButton("Add Room", getClassroomIcon(),
                () -> {
                    try {
                        switchCenter(RoomScreen.room(this));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = createSidebarButton("Logout", getLogoutIcon(),
                () -> switchCenter(new Pane()));

        sidebar.getChildren().addAll(dashboard, arrangement, room, spacer, logout);

        return sidebar;
    }

    private Button createSidebarButton(String text, SVGPath icon, Runnable action) {

        Button btn = new Button(text);
        btn.setGraphic(icon);

        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setGraphicTextGap(10);
        btn.getStyleClass().add("sidebar-btn");

        // Hover effect
        btn.setOnMouseEntered(e -> icon.setFill(Color.BLUE));
        btn.setOnMouseExited(e -> icon.setFill(Color.GRAY));

        // Click action
        btn.setOnAction(e -> {
            highlightActive(btn);
            action.run();
        });

        return btn;
    }

    private void highlightActive(Button btn) {

        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-active");
        }

        btn.getStyleClass().add("sidebar-active");
        activeButton = btn;
    }

    //  DASHBOARD

    private HBox createDeveloperSection() {

        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(15));
        container.setPrefHeight(80);
        container.setPadding(new Insets(10, 20, 10, 20));
        container.getStyleClass().add("glass");

        Label devs = new Label("Developed By:");
        devs.getStyleClass().add("dashboard-title");

        HBox devRow = new HBox(25);
        devRow.setAlignment(Pos.CENTER);


        HBox dev1 = createDeveloperCard("E-SAPlogo.jpg", "Name 1", "CSE-A");
        HBox dev2 = createDeveloperCard("E-SAPlogo.jpg", "Name 2", "CSE-A");
        HBox dev3 = createDeveloperCard("E-SAPlogo.jpg", "Name 3", "CSE-A");

        devRow.getChildren().addAll(dev1, dev2, dev3);

        container.getChildren().addAll(devs, devRow);

        return container;
    }

    private HBox createDeveloperCard(String imagePath, String name, String section) {

        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);

//        ImageView image = new ImageView(
//                new Image(getClass().getResourceAsStream("com/planner/GUI/" + imagePath))
//        );
//
//        image.setFitWidth(50);
//        image.setFitHeight(50);

        //  Make circular image
//        Circle clip = new Circle(25, 25, 25);
//        image.setClip(clip);

        VBox devdetail = new VBox(3);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold;");

        Label sectionLabel = new Label(section);
        sectionLabel.setStyle("-fx-text-fill: #cfd8dc; -fx-font-size: 11;");

        devdetail.getChildren().addAll(nameLabel, sectionLabel);

        card.getChildren().addAll(devdetail);

        return card;
    }

    //  CARD


    //  ROOM SCREEN

    //  FADE SWITCH
    public void switchCenter(Node newContent) {
        Node current = rightSide.getCenter();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), current);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            rightSide.setCenter(newContent);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), newContent);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}