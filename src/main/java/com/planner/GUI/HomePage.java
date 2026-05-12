package com.planner.GUI;

import com.planner.GUI.Screens.AboutScreen;
import com.planner.GUI.Screens.DashboardScreen;
import com.planner.GUI.Screens.UploadScreen;
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

import static com.planner.GUI.Screens.TeacherScreen.teacherScreen;


public class HomePage extends Application {

    private BorderPane root;
    public static BorderPane rightSide;
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

        msg.setOpacity(0);
        msg.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

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

    public BorderPane createDashboardScreen() {

        BorderPane rootLayout = new BorderPane();

        VBox sidebar = createSidebar();
        rootLayout.setLeft(sidebar);

        rightSide = new BorderPane();

        rightSide.setTop(createTopBar("Dashboard"));
        rightSide.setCenter(DashboardScreen.dashboardContent(this));

        rootLayout.setCenter(rightSide);

        return rootLayout;
    }
    public static HBox createTopBar(String titleText){

        HBox topBar = new HBox();
        topBar.setPrefHeight(60);
        topBar.setPadding(new Insets(0, 25, 0, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);

        topBar.getStyleClass().add("top-bar");

// LEFT TITLE (FIXED STYLE)
        Label title = new Label(titleText);
        title.getStyleClass().add("title");

// SPACER (CRITICAL)
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

// SEARCH FIELD (FIXED LOOK)
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

// AVATAR (FIX SIZE)
        Circle avatar = new Circle(14);
        avatar.setFill(Color.LIGHTGRAY);

// RIGHT SECTION (IMPORTANT SPACING)
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
                "M3 7h18v10H3V7zm2 2v6h14V9H5zM7 17v2h2v-2H7zm8 0v2h2v-2h-2z"
        );

        icon.setStyle("-fx-fill: #6B7280;");
        return icon;
    }

    private SVGPath getTeacherIcon() {

        SVGPath icon = new SVGPath();

        icon.setContent(
                "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5s-3 1.34-3 3 1.34 3 3 3zM8 11c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5C15 14.17 10.33 13 8 13zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"
        );

        icon.setStyle("-fx-fill: #6B7280;");

        return icon;
    }

    private SVGPath getAboutIcon() {
        SVGPath icon = new SVGPath();

        icon.setContent(
                "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 " +
                        "10-4.48 10-10S17.52 2 12 2zm0 4a1.5 1.5 0 110 3 " +
                        "1.5 1.5 0 010-3zm2 12h-4v-2h1v-4h-1v-2h3v6h1v2z"
        );

        icon.setStyle("-fx-fill: #6B7280;");
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
                () -> switchScreen(createTopBar("Dashboard"), DashboardScreen.dashboardContent(this)));

        Button arrangements = createSidebarButton("Arrangements", getArrangementIcon(), false,
                () -> switchScreen(createTopBar("Arrangements"), UploadScreen.dataScreen(this)));

        Button showRoomBtn = createSidebarButton("Show Room", getRoomIcon(), false,
                () -> {
                    try {
                        switchScreen(createTopBar("Show Room"), RoomScreen.room(this));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

        Button teachers = createSidebarButton("Teachers", getTeacherIcon(), false,
                () -> switchScreen(createTopBar("Teachers"), teacherScreen(this)));


        menu.getChildren().addAll(dashboard, arrangements, showRoomBtn, teachers);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

// BOTTOM SECTION
        VBox bottom = new VBox(10);
        bottom.setAlignment(Pos.BOTTOM_CENTER);
        bottom.setPadding(new Insets(0, 0, 30, 0));

        Button confiiguration = createSidebarButton("Confiiguration", getAboutIcon() , false,
                () -> switchScreen(createTopBar("Confiiguration"), newPane(this)));
        confiiguration.setAlignment(Pos.CENTER);

        Button about = createSidebarButton("About", getAboutIcon() , false,
                () -> switchScreen(createTopBar("About"), AboutScreen.about(this)));
        about.setAlignment(Pos.CENTER);

        Button logout = createSidebarButton("Logout", getLogoutIcon(), false,
                () -> switchScreen(createTopBar("Logout"), new Pane()));
        logout.setAlignment(Pos.CENTER);

        bottom.getChildren().addAll(confiiguration, about, logout);

        sidebar.getChildren().addAll(logoBox, menu,spacer, bottom);

        return sidebar;
    }

    private Node newPane(HomePage homePage) {

        StackPane pane = new StackPane();

        Label label = new Label("Coming Soon");

        pane.getChildren().add(label);

        return pane;
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

            if (activeButton.getGraphic() instanceof SVGPath oldIcon) {
                oldIcon.setStyle("-fx-fill: #6B7280;");
            }
        }

        if (!btn.getStyleClass().contains("sidebar-active")) {
            btn.getStyleClass().add("sidebar-active");
        }

        if (btn.getGraphic() instanceof SVGPath newIcon) {
            newIcon.setStyle("-fx-fill: #2563EB;");
        }

        activeButton = btn;
    }

// FADE SWITCH

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