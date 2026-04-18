package com.planner.GUI;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.sql.SQLException;
import javafx.scene.Node;

public class HomePage extends Application {

    private BorderPane root;
    private BorderPane rightSide;
    private Button activeButton = null;

    @Override
    public void start(Stage stage) throws SQLException {

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

    private BorderPane createDashboardScreen() throws SQLException {

        BorderPane rootLayout = new BorderPane();

        VBox sidebar = createSidebar();
        rootLayout.setLeft(sidebar);

        rightSide = new BorderPane();

        rightSide.setTop(createTopBar("Dashboard"));
        rightSide.setCenter(Screens.dashboardContent(this));

        rootLayout.setCenter(rightSide);

        return rootLayout;
    }
    private HBox createTopBar(String titleText){

        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setPadding(new Insets(0, 25, 0, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);

        topBar.getStyleClass().add("top-bar");

        //  LEFT TITLE (FIXED STYLE)
        Label title = new Label(titleText);
        title.getStyleClass().add("title");

        //  SPACER (CRITICAL)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        //  SEARCH FIELD (FIXED LOOK)
        TextField search = new TextField();
        search.setPromptText("Search...");
        search.setPrefWidth(220);
        search.setPrefHeight(36);

        search.getStyleClass().add("search-box");

        Label bell = new Label("🔔");
        bell.setStyle("-fx-font-size: 16px;");
        bell.setMinWidth(30);
        bell.setAlignment(Pos.CENTER);

        Label user = new Label("Admin User");
        user.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #111827;"
        );

        //  AVATAR (FIX SIZE)
        Circle avatar = new Circle(14);
        avatar.setFill(Color.LIGHTGRAY);

        //  RIGHT SECTION (IMPORTANT SPACING)
        HBox right = new HBox(12, search, bell, user, avatar);
        right.setAlignment(Pos.CENTER_RIGHT);

        topBar.getChildren().addAll(title, spacer, right);

        return topBar;
    }

    private SVGPath getDashboardIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M3 13h8V3H3v10zm10 8h8V3h-8v18zM3 21h8v-6H3v6zm10 0h8v-6h-8v6z"
        );

        icon.setStyle("-fx-fill: #6B7280;");
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getArrangementIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M3 6h18v2H3V6zm0 5h18v2H3v-2zm0 5h18v2H3v-2z"
        );

        icon.setStyle("-fx-fill: #6B7280;");
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getRoomIcon() {
        SVGPath icon = new SVGPath();

        icon.setContent(
                "M4 3h12v14H4V3zm2 2v10h8V5H6zm1 2h2v2H7V7zm4 0h2v2h-2V7zm-4 4h2v2H7v-2zm4 0h2v2h-2v-2z"
        );
        icon.setStyle("-fx-fill: #6B7280;");
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private SVGPath getLogoutIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M16 13v-2H7V8l-5 4 5 4v-3h9zm3-10H9a2 2 0 00-2 2v3h2V5h10v14H9v-3H7v3a2 2 0 002 2h10a2 2 0 002-2V5a2 2 0 00-2-2z"
        );
        icon.setStyle("-fx-fill: #6B7280;");
        icon.setScaleX(1.2);
        icon.setScaleY(1.2);

        return icon;
    }

    private VBox createSidebar() {

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(230);
        sidebar.getStyleClass().add("sidebar");

        // LOGO SECTION
        VBox logoBox = new VBox(5);
        Label title = new Label("E - SAP");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label subtitle = new Label("ADMIN");
        subtitle.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10;");

        logoBox.getChildren().addAll(title, subtitle);

        // MENU
        VBox menu = new VBox(10);

        Button dashboard = createSidebarButton("Dashboard", getDashboardIcon(), true,
                () -> {
                    switchScreen(createTopBar("Dashboard"), Screens.dashboardContent(this));
                });

        Button students = createSidebarButton("Arrangements", getArrangementIcon(), false,
                () -> switchScreen(createTopBar("Arrangements"), new Pane()));


        Button data = createSidebarButton("Data Import", getArrangementIcon(), false,
                () -> switchScreen(createTopBar("Data Import"), new Pane()));

        menu.getChildren().addAll(dashboard, students, data);

        //  PRIMARY BUTTON
        Button newExam = new Button("+ New Exam");
        newExam.getStyleClass().add("primary-btn");
        newExam.setPrefWidth(180);

        //  BOTTOM SECTION
        VBox bottom = new VBox(10);

        Button settings = new Button("Settings");
        Button support = new Button("Support");

        settings.getStyleClass().add("sidebar-secondary");
        support.getStyleClass().add("sidebar-secondary");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = new Button("Logout");
        logout.getStyleClass().add("sidebar-secondary");

        bottom.getChildren().addAll(newExam, settings, support, spacer, logout);

        sidebar.getChildren().addAll(logoBox, menu, bottom);

        return sidebar;
    }
    private Button createSidebarButton(String text, SVGPath icon, boolean active, Runnable action) {

        Button btn = new Button(text);
        btn.setGraphic(icon);
        btn.setContentDisplay(ContentDisplay.LEFT);
        btn.setGraphicTextGap(10);

        btn.setMaxWidth(Double.MAX_VALUE);

        btn.getStyleClass().add("sidebar-btn");

        if (active) {
            btn.getStyleClass().add("sidebar-active");
            icon.setStyle("-fx-fill: #2563EB;");
        }

        btn.setOnAction(e -> {
            highlightActive(btn);
            action.run();
        });

        return btn;
    }
    private void highlightActive(Button btn) {

        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-active");

            // reset icon color
            ((SVGPath) activeButton.getGraphic()).setStyle("-fx-fill: #6B7280;");
        }

        btn.getStyleClass().add("sidebar-active");

        // set active icon color
        ((SVGPath) btn.getGraphic()).setStyle("-fx-fill: #2563EB;");

        activeButton = btn;
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

    public void switchScreen(Node topBar, Node content) {
        rightSide.setTop(topBar);
        switchCenter(content);
    }

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