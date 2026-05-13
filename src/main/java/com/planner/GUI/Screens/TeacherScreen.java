package com.planner.GUI.Screens;

import com.planner.Database.TeacherDB;
import com.planner.GUI.CardComponent;
import com.planner.GUI.HomePage;
import com.planner.GUI.Notification;
import com.planner.GUI.Teacher;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeacherScreen {

    public static Node teacherScreen(HomePage app) {

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F9FAFB;");

        // ── TOP STATS BAR ──────────────────────────────────────────
        List<Teacher> allTeachers = TeacherDB.fetchTeachers();

        long maleCount   = allTeachers.stream()
                .filter(t -> "Male".equalsIgnoreCase(t.getGender()))
                .count();
        long femaleCount = allTeachers.stream()
                .filter(t -> "Female".equalsIgnoreCase(t.getGender()))
                .count();

        long branchCount = allTeachers.stream()
                .map(Teacher::getBranch)
                .filter(b -> b != null && !b.isEmpty())
                .distinct()
                .count();

        HBox statsBar = new HBox(16);
        statsBar.setPadding(new Insets(28, 36, 0, 36));
        statsBar.getChildren().addAll(
                statCard("Total Faculty",  String.valueOf(allTeachers.size()), "groups",      "#0056D2"),
                statCard("Male",           String.valueOf(maleCount),          "man",          "#0891B2"),
                statCard("Female",         String.valueOf(femaleCount),         "woman",        "#7C3AED")
               // statCard("Branches", String.valueOf(branchCount), "account_tree", "#059669")
        );

        // ── HEADER ROW ─────────────────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setPadding(new Insets(24, 36, 12, 36));
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label heading = new Label("Faculty Directory");
        heading.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: 800;
            -fx-text-fill: #111827;
            """);
        Label subheading = new Label(allTeachers.size() + " invigilators registered");
        subheading.setStyle("""
            -fx-font-size: 12px;
            -fx-text-fill: #6B7280;
            """);
        titleBox.getChildren().addAll(heading, subheading);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search
        HBox searchBox = new HBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 0 12 0 12;
            """);
        Label searchIcon = new Label("🔍");
        searchIcon.setStyle("-fx-font-size: 12px;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search faculty...");
        searchField.setPrefWidth(220);
        searchField.setPrefHeight(36);
        searchField.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-font-size: 13px;
            -fx-text-fill: #111827;
            """);
        searchBox.getChildren().addAll(searchIcon, searchField);

        // Add Teacher button
        Button addBtn = new Button("+ Add Teacher");
        addBtn.setStyle("""
            -fx-background-color: #0056D2;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-padding: 9 20 9 20;
            -fx-cursor: hand;
            """);
        addBtn.setOnAction(e -> showAddDialog(app));

        headerRow.getChildren().addAll(titleBox, spacer, searchBox, addBtn);
        HBox.setMargin(addBtn, new Insets(0, 0, 0, 12));

        // ── TABLE HEADER ───────────────────────────────────────────
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(10, 36, 10, 36));
        tableHeader.setStyle("-fx-background-color: #F3F4F6; -fx-border-color: transparent transparent #E5E7EB transparent; -fx-border-width: 1;");
        tableHeader.setAlignment(Pos.CENTER_LEFT);

        String[] cols = {"#", "Name", "Gender", "Branch", "Actions"};
        double[] widths = {40, 300, 120, 180, 200};
        for (int i = 0; i < cols.length; i++) {
            Label col = new Label(cols[i].toUpperCase());
            col.setPrefWidth(widths[i]);
            col.setStyle("""
                -fx-font-size: 10px;
                -fx-font-weight: 700;
                -fx-text-fill: #6B7280;
                -fx-letter-spacing: 0.1em;
                """);
            tableHeader.getChildren().add(col);
        }

        // ── TEACHER LIST ───────────────────────────────────────────
        VBox listContainer = new VBox(0);
        listContainer.setStyle("-fx-background-color: white;");

        populateList(listContainer, allTeachers, app, 0);

        // Search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            List<Teacher> filtered = allTeachers.stream()
                    .filter(t -> t.getName().toLowerCase().contains(newVal.toLowerCase())
                            || (t.getBranch() != null && t.getBranch().toLowerCase().contains(newVal.toLowerCase())))
                    .collect(Collectors.toList());
            listContainer.getChildren().clear();
            populateList(listContainer, filtered, app, 0);
        });

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(400);
        scroll.setPrefHeight(400);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // ── UPLOAD SECTION ─────────────────────────────────────────
        HBox uploadSection = new HBox(16);
        uploadSection.setAlignment(Pos.CENTER);
        uploadSection.setPadding(new Insets(20, 36, 20, 36));
        uploadSection.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB transparent transparent transparent;
            -fx-border-width: 1;
            """);

        StackPane uploadCard = CardComponent.createCard(
                "Upload Excel/CSV",
                "Bulk import teachers from file",
                "/upload.png",
                () -> UploadScreen.openFileChooser(app)
        );

        StackPane manualCard = CardComponent.createCard(
                "Enter Manually",
                "Add one teacher at a time",
                "/upload.png",
                () -> app.switchCenter(showManualEntryDialog(app))
        );

        uploadSection.getChildren().addAll(uploadCard, manualCard);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #F1F1F1;");

        root.getChildren().addAll(
                statsBar,
                uploadSection,
                headerRow,
                sep1,
                tableHeader,
                scroll
        );

        ScrollPane mainScroll = new ScrollPane(root);
        mainScroll.setFitToWidth(true);
        mainScroll.setFitToHeight(false);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScroll.setStyle("""
    -fx-background-color: #F9FAFB;
    -fx-background: #F9FAFB;
    """);

        return mainScroll;
    }

    // ── ROW BUILDER ────────────────────────────────────────────────
    private static void populateList(
            VBox container,
            List<Teacher> teachers,
            HomePage app,
            int startIndex
    ) {
        if (teachers.isEmpty()) {
            Label empty = new Label("No teachers found");
            empty.setStyle("""
                -fx-font-size: 13px;
                -fx-text-fill: #9CA3AF;
                -fx-padding: 40;
                """);
            container.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < teachers.size(); i++) {
            Teacher t   = teachers.get(i);
            boolean odd = (i % 2 == 0);

            HBox row = new HBox();
            row.setPadding(new Insets(14, 36, 14, 36));
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle(
                    "-fx-background-color: " + (odd ? "white" : "#FAFAFA") + ";" +
                            "-fx-border-color: transparent transparent #F3F4F6 transparent;" +
                            "-fx-border-width: 1;"
            );

            // Hover
            final String base = odd ? "white" : "#FAFAFA";
            row.setOnMouseEntered(e -> row.setStyle(
                    "-fx-background-color: #EFF6FF;" +
                            "-fx-border-color: transparent transparent #DBEAFE transparent;" +
                            "-fx-border-width: 1;"
            ));
            row.setOnMouseExited(e -> row.setStyle(
                    "-fx-background-color: " + base + ";" +
                            "-fx-border-color: transparent transparent #F3F4F6 transparent;" +
                            "-fx-border-width: 1;"
            ));

            // Index
            Label idx = new Label(String.valueOf(i + 1));
            idx.setPrefWidth(40);
            idx.setStyle("-fx-font-size: 12px; -fx-text-fill: #9CA3AF; -fx-font-weight: 600;");

            // Avatar + Name
            HBox nameCell = new HBox(10);
            nameCell.setAlignment(Pos.CENTER_LEFT);
            nameCell.setPrefWidth(300);

            String initials = getInitials(t.getName());
            boolean isMale  = "Male".equalsIgnoreCase(t.getGender());
            Label avatar = new Label(initials);
            avatar.setMinSize(34, 34);
            avatar.setMaxSize(34, 34);
            avatar.setAlignment(Pos.CENTER);
            avatar.setStyle(
                    "-fx-background-color: " + (isMale ? "#DBEAFE" : "#EDE9FE") + ";" +
                            "-fx-text-fill: "         + (isMale ? "#1D4ED8" : "#6D28D9") + ";" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-background-radius: 50%;"
            );

            VBox nameInfo = new VBox(1);
            Label nameLabel = new Label(t.getName());
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #111827;");
            nameInfo.getChildren().add(nameLabel);
            if (t.getEmail() != null && !t.getEmail().isEmpty()) {
                Label emailLabel = new Label(t.getEmail());
                emailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
                nameInfo.getChildren().add(emailLabel);
            }
            nameCell.getChildren().addAll(avatar, nameInfo);

            // Gender badge
            Label genderBadge = new Label(t.getGender());
            genderBadge.setPrefWidth(120);
            genderBadge.setStyle(
                    "-fx-background-color: " + (isMale ? "#EFF6FF" : "#F5F3FF") + ";" +
                            "-fx-text-fill: "         + (isMale ? "#2563EB" : "#7C3AED") + ";" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-weight: 700;" +
                            "-fx-padding: 3 10 3 10;" +
                            "-fx-background-radius: 20;"
            );

            // Branch
            Label branchLabel = new Label(
                    (t.getBranch() != null && !t.getBranch().isEmpty()) ? t.getBranch() : "—"
            );
            branchLabel.setPrefWidth(180);
            branchLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4B5563;");

            // Actions
            HBox actions = new HBox(10);
            actions.setPrefWidth(200);
            actions.setAlignment(Pos.CENTER_LEFT);

            Button editBtn = iconBtn(
                    "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z",
                    "#0056D2", "#EFF6FF", "#003FA3", "Edit"   // ← darker blue hover
            );
            Button deleteBtn = iconBtn(
                    "M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z",
                    "#DC2626", "#FEF2F2", "#991B1B", "Delete" // ← darker red hover
            );

            editBtn.setOnAction(e -> showEditDialog(t, app));
            deleteBtn.setOnAction(e -> showDeleteConfirm(t, app));

            actions.getChildren().addAll(editBtn, deleteBtn);

            row.getChildren().addAll(idx, nameCell, genderBadge, branchLabel, actions);
            container.getChildren().add(row);
        }
    }

    // ── DIALOGS ────────────────────────────────────────────────────
    private static void showAddDialog(HomePage app) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Teacher");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("""
            -fx-background-color: white;
            -fx-font-family: 'Segoe UI';
            """);
        pane.setPrefWidth(420);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20, 24, 8, 24));

        Label title = new Label("Add New Teacher");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #111827;");

        TextField nameField   = styledField("Full Name");
        TextField branchField = styledField("Branch (e.g. CS, CE)");
        TextField phoneField  = styledField("Phone Number");
        TextField emailField  = styledField("Email Address");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");
        genderBox.setValue("Male");
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 6;
            -fx-font-size: 13px;
            """);

        content.getChildren().addAll(
                title,
                fieldGroup("Full Name", nameField),
                fieldGroup("Gender",    genderBox),
                fieldGroup("Branch",    branchField),
                fieldGroup("Phone",     phoneField),
                fieldGroup("Email",     emailField)
        );

        pane.setContent(content);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style OK button
        Button okBtn = (Button) pane.lookupButton(ButtonType.OK);
        okBtn.setText("Add Teacher");
        okBtn.setStyle("""
            -fx-background-color: #0056D2;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
            """);

        Button cancelBtn = (Button) pane.lookupButton(ButtonType.CANCEL);
        cancelBtn.setText("Cancel");
        cancelBtn.setStyle("""
    -fx-background-color: #F3F4F6;
    -fx-text-fill: #374151;
    -fx-font-weight: 700;
    -fx-background-radius: 8;
    -fx-padding: 8 20 8 20;
    -fx-border-color: #E5E7EB;
    -fx-border-radius: 8;
    -fx-cursor: hand;
    """);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    Notification.message("Name cannot be empty.");
                    return;
                }
                TeacherDB.addTeacher(
                        name,
                        genderBox.getValue(),
                        branchField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim()
                );
                Notification.message("Teacher added successfully.");
                app.switchCenter(teacherScreen(app));
            }
        });
    }

    private static void showEditDialog(Teacher teacher, HomePage app) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Teacher");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("-fx-background-color: white;");
        pane.setPrefWidth(420);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20, 24, 8, 24));

        Label title = new Label("Edit Teacher");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #111827;");

        TextField nameField = styledField("Full Name");
        nameField.setText(teacher.getName());

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");
        genderBox.setValue(teacher.getGender());
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 6;
            -fx-font-size: 13px;
            """);

        content.getChildren().addAll(
                title,
                fieldGroup("Full Name", nameField),
                fieldGroup("Gender",    genderBox)
        );

        pane.setContent(content);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) pane.lookupButton(ButtonType.OK);
        okBtn.setText("Save Changes");
        okBtn.setStyle("""
            -fx-background-color: #0056D2;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-background-radius: 8;
            -fx-padding: 8 20 8 20;
            """);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String newName = nameField.getText().trim();
                if (newName.isEmpty()) {
                    Notification.message("Name cannot be empty.");
                    return;
                }
                TeacherDB.updateTeacher(
                        teacher.getId(),
                        newName,
                        genderBox.getValue()
                );
                Notification.message("Teacher updated successfully.");
                app.switchCenter(teacherScreen(app));
            }
        });
    }

    private static void showDeleteConfirm(Teacher teacher, HomePage app) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Teacher");
        alert.setHeaderText("Delete " + teacher.getName() + "?");
        alert.setContentText("This action cannot be undone.");

        DialogPane pane = alert.getDialogPane();
        pane.setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI';");

        Button deleteBtn = (Button) pane.lookupButton(ButtonType.OK);
        deleteBtn.setText("Delete");
        deleteBtn.setStyle("""
            -fx-background-color: #DC2626;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-background-radius: 8;
            """);

        Button cancelBtn = (Button) pane.lookupButton(ButtonType.CANCEL);
        cancelBtn.setStyle("""
    -fx-background-color: #F3F4F6;
    -fx-text-fill: #374151;
    -fx-font-weight: 700;
    -fx-background-radius: 8;
    -fx-padding: 8 20 8 20;
    -fx-border-color: #E5E7EB;
    -fx-border-radius: 8;
    -fx-cursor: hand;
    """);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                TeacherDB.deleteTeacher(teacher.getId());
                Notification.message("Teacher deleted.");
                app.switchCenter(teacherScreen(app));
            }
        });
    }

    // ── HELPERS ────────────────────────────────────────────────────
    private static VBox statCard(String label, String value, String icon, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setPrefWidth(180);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #F1F1F1;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);

        Label lbl = new Label(label.toUpperCase());
        lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: 700; -fx-text-fill: #9CA3AF; -fx-letter-spacing: 0.1em;");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    private static VBox miniUploadCard(String title, String sub, String icon, HomePage app, Runnable action) {

        VBox card = new VBox(16);
        card.setPadding(new Insets(24, 28, 24, 28));
        card.setPrefWidth(240);
        card.setPrefHeight(160);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #E5E7EB;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);
        -fx-cursor: hand;
        """);

        // Icon circle
        Label iconLabel = new Label(
                icon.equals("upload_file") ? "📂" : "✏️"
        );
        iconLabel.setStyle("-fx-font-size: 28px;");

        StackPane iconCircle = new StackPane(iconLabel);
        iconCircle.setMinSize(56, 56);
        iconCircle.setMaxSize(56, 56);
        iconCircle.setStyle("""
        -fx-background-color: #EFF6FF;
        -fx-background-radius: 50%;
        """);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
        -fx-font-size: 14px;
        -fx-font-weight: 800;
        -fx-text-fill: #111827;
        """);

        // Subtitle
        Label subLabel = new Label(sub);
        subLabel.setStyle("""
        -fx-font-size: 11px;
        -fx-text-fill: #6B7280;
        """);
        subLabel.setWrapText(true);
        subLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(iconCircle, titleLabel, subLabel);

        // Hover effect — CardComponent jaisa
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.04);
            card.setScaleY(1.04);
            card.setTranslateY(-4);
            card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #0056D2;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,86,210,0.15), 12, 0, 0, 4);
            -fx-cursor: hand;
            """);
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1);
            card.setScaleY(1);
            card.setTranslateY(0);
            card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);
            -fx-cursor: hand;
            """);
        });

        card.setOnMouseClicked(e -> action.run());

        return card;
    }

    private static Button iconBtn(String svgPath, String iconColor, String bgColor, String hoverColor, String tooltip) {
        javafx.scene.shape.SVGPath svg = new javafx.scene.shape.SVGPath();
        svg.setContent(svgPath);
        svg.setFill(javafx.scene.paint.Color.web(iconColor));
        svg.setScaleX(0.72);
        svg.setScaleY(0.72);

        StackPane iconPane = new StackPane(svg);
        iconPane.setMinSize(16, 16);
        iconPane.setMaxSize(16, 16);

        Button btn = new Button();
        btn.setGraphic(iconPane);
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 8 7 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: transparent;" +
                        "-fx-min-width: 34px;" +
                        "-fx-min-height: 34px;"
        );

        // Hover — darker shade + white icon
        btn.setOnMouseEntered(e -> {
            svg.setFill(javafx.scene.paint.Color.WHITE);
            btn.setStyle(
                    "-fx-background-color: " + hoverColor + ";" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 7 8 7 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: transparent;" +
                            "-fx-min-width: 34px;" +
                            "-fx-min-height: 34px;"
            );
        });

        // Mouse exit — original style wapas
        btn.setOnMouseExited(e -> {
            svg.setFill(javafx.scene.paint.Color.web(iconColor));
            btn.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 7 8 7 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-border-color: transparent;" +
                            "-fx-min-width: 34px;" +
                            "-fx-min-height: 34px;"
            );
        });

        Tooltip tip = new Tooltip(tooltip);
        tip.setStyle("-fx-font-size: 11px; -fx-background-radius: 6;");
        Tooltip.install(btn, tip);

        return btn;
    }

    private static TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 9 12 9 12;
            -fx-font-size: 13px;
            """);
        return f;
    }

    private static VBox fieldGroup(String label, Node field) {
        VBox group = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: #374151;");
        group.getChildren().addAll(lbl, field);
        return group;
    }

    private static String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private static final List<Teacher> sessionAdded = new ArrayList<>();

    public static Node showManualEntryDialog(HomePage app) {

        sessionAdded.clear();

        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #F9FAFB;");

        // ── TOP BAR ───────────────────────────────────────────────
        HBox topBar = new HBox(14);
        topBar.setPadding(new Insets(16, 32, 16, 32));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("""
            -fx-background-color: white;
            -fx-border-color: transparent transparent #E5E7EB transparent;
            -fx-border-width: 1;
            """);

        Button closeBtn = new Button("✕  Close & Go Back");
        closeBtn.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #374151;
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 7 16 7 16;
            -fx-cursor: hand;
            """);
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("""
            -fx-background-color: #FEF2F2;
            -fx-text-fill: #DC2626;
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-border-color: #FCA5A5;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 7 16 7 16;
            -fx-cursor: hand;
            """));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #374151;
            -fx-font-size: 12px;
            -fx-font-weight: 700;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 7 16 7 16;
            -fx-cursor: hand;
            """));
        closeBtn.setOnAction(e -> app.switchCenter(TeacherScreen.teacherScreen(app)));

        VBox titleBox = new VBox(2);
        Label heading = new Label("Add Teachers Manually");
        heading.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: 800;
            -fx-text-fill: #111827;
            """);
        Label sub = new Label("Fill form → Add → Repeat. Close when done.");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
        titleBox.getChildren().addAll(heading, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Session count badge
        Label countBadge = new Label("0 added this session");
        countBadge.setStyle("""
            -fx-background-color: #EFF6FF;
            -fx-text-fill: #0056D2;
            -fx-font-size: 11px;
            -fx-font-weight: 700;
            -fx-padding: 4 12 4 12;
            -fx-background-radius: 20;
            """);

        topBar.getChildren().addAll(closeBtn, titleBox, spacer, countBadge);

        // ── MAIN AREA ─────────────────────────────────────────────
        HBox mainArea = new HBox(0);
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        // LEFT — Form
        VBox formPanel = new VBox(0);
        formPanel.setPrefWidth(480);
        formPanel.setMinWidth(480);
        formPanel.setStyle("""
            -fx-background-color: white;
            -fx-border-color: transparent #E5E7EB transparent transparent;
            -fx-border-width: 1;
            """);

        // Form header
        VBox formHeader = new VBox(4);
        formHeader.setPadding(new Insets(24, 28, 16, 28));
        formHeader.setStyle("""
            -fx-border-color: transparent transparent #F3F4F6 transparent;
            -fx-border-width: 1;
            """);
        Label formTitle = new Label("Teacher Information");
        formTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: #111827;");
        Label formSub = new Label("Fields marked * are required");
        formSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
        formHeader.getChildren().addAll(formTitle, formSub);

        // Form fields
        VBox fields = new VBox(16);
        fields.setPadding(new Insets(22, 28, 0, 28));

        TextField nameField   = styledField("e.g. Rahul Sharma");
        TextField branchField = styledField("e.g. CS, CE, ME");
        TextField phoneField  = styledField("e.g. 9876543210");
        TextField emailField  = styledField("e.g. rahul@sistec.ac.in");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");
        genderBox.setValue("Male");
        genderBox.setMaxWidth(Double.MAX_VALUE);
        genderBox.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 8;
            -fx-font-size: 13px;
            """);

        // Live preview inside form
        HBox livePreview = new HBox(12);
        livePreview.setAlignment(Pos.CENTER_LEFT);
        livePreview.setPadding(new Insets(12, 14, 12, 14));
        livePreview.setStyle("""
            -fx-background-color: #F9FAFB;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);

        Label previewAvatar = new Label("?");
        previewAvatar.setMinSize(40, 40);
        previewAvatar.setMaxSize(40, 40);
        previewAvatar.setAlignment(Pos.CENTER);
        previewAvatar.setStyle("""
            -fx-background-color: #DBEAFE;
            -fx-text-fill: #1D4ED8;
            -fx-font-size: 14px;
            -fx-font-weight: 800;
            -fx-background-radius: 50%;
            """);

        VBox previewInfo = new VBox(2);
        Label previewName = new Label("Name will appear here");
        previewName.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #9CA3AF;");
        Label previewGenderLabel = new Label("Gender");
        previewGenderLabel.setStyle("""
            -fx-background-color: #EFF6FF;
            -fx-text-fill: #2563EB;
            -fx-font-size: 10px;
            -fx-font-weight: 700;
            -fx-padding: 2 8 2 8;
            -fx-background-radius: 20;
            """);
        previewInfo.getChildren().addAll(previewName, previewGenderLabel);
        livePreview.getChildren().addAll(previewAvatar, previewInfo);

        // Live update bindings
        nameField.textProperty().addListener((obs, old, val) -> {
            if (val.trim().isEmpty()) {
                previewAvatar.setText("?");
                previewName.setText("Name will appear here");
                previewName.setStyle("-fx-font-size:13px; -fx-font-weight:700; -fx-text-fill:#9CA3AF;");
            } else {
                previewAvatar.setText(getInitials(val));
                previewName.setText(val.trim());
                previewName.setStyle("-fx-font-size:13px; -fx-font-weight:700; -fx-text-fill:#111827;");
            }
        });

        genderBox.valueProperty().addListener((obs, old, val) -> {
            boolean male = "Male".equals(val);
            previewAvatar.setStyle(
                    "-fx-background-color:" + (male ? "#DBEAFE" : "#EDE9FE") + ";" +
                            "-fx-text-fill:"        + (male ? "#1D4ED8" : "#6D28D9") + ";" +
                            "-fx-font-size:14px; -fx-font-weight:800; -fx-background-radius:50%;"
            );
            previewGenderLabel.setText(val);
            previewGenderLabel.setStyle(
                    "-fx-background-color:" + (male ? "#EFF6FF" : "#F5F3FF") + ";" +
                            "-fx-text-fill:"        + (male ? "#2563EB" : "#7C3AED") + ";" +
                            "-fx-font-size:10px; -fx-font-weight:700;" +
                            "-fx-padding: 2 8 2 8; -fx-background-radius:20;"
            );
        });

        fields.getChildren().addAll(
                livePreview,
                fieldGroup("Full Name *",   nameField),
                fieldGroup("Gender *",      genderBox),
                fieldGroup("Branch",        branchField),
                fieldGroup("Phone Number",  phoneField),
                fieldGroup("Email Address", emailField)
        );

        // Add button
        VBox addBtnBox = new VBox();
        addBtnBox.setPadding(new Insets(20, 28, 24, 28));
        Button addBtn = new Button("Add Teacher →");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("""
            -fx-background-color: #0056D2;
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-font-size: 14px;
            -fx-background-radius: 10;
            -fx-padding: 13 0 13 0;
            -fx-cursor: hand;
            """);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("""
            -fx-background-color: #0041A8;
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-font-size: 14px;
            -fx-background-radius: 10;
            -fx-padding: 13 0 13 0;
            -fx-cursor: hand;
            """));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("""
            -fx-background-color: #0056D2;
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-font-size: 14px;
            -fx-background-radius: 10;
            -fx-padding: 13 0 13 0;
            -fx-cursor: hand;
            """));
        addBtnBox.getChildren().add(addBtn);

        formPanel.getChildren().addAll(formHeader, fields, addBtnBox);

        // RIGHT — Added This Session list
        VBox rightPanel = new VBox(0);
        rightPanel.setStyle("-fx-background-color: #F9FAFB;");
        VBox.setVgrow(rightPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Right header
        HBox rightHeader = new HBox();
        rightHeader.setPadding(new Insets(20, 24, 16, 24));
        rightHeader.setAlignment(Pos.CENTER_LEFT);
        rightHeader.setStyle("""
            -fx-background-color: white;
            -fx-border-color: transparent transparent #E5E7EB transparent;
            -fx-border-width: 1;
            """);

        Label rightTitle = new Label("Added This Session");
        rightTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #111827;");

        Region rSpacer = new Region();
        HBox.setHgrow(rSpacer, Priority.ALWAYS);

        Label rightCount = new Label("0 teachers");
        rightCount.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #9CA3AF;");

        rightHeader.getChildren().addAll(rightTitle, rSpacer, rightCount);

        // Session list container
        VBox sessionList = new VBox(8);
        sessionList.setPadding(new Insets(16));

        Label emptyLabel = new Label("No teachers added yet.\nFill the form and press 'Add Teacher'.");
        emptyLabel.setStyle("""
            -fx-font-size: 12px;
            -fx-text-fill: #D1D5DB;
            -fx-alignment: center;
            -fx-text-alignment: center;
            """);
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);
        sessionList.getChildren().add(emptyLabel);

        ScrollPane rightScroll = new ScrollPane(sessionList);
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(rightScroll, Priority.ALWAYS);

        rightPanel.getChildren().addAll(rightHeader, rightScroll);

        mainArea.getChildren().addAll(formPanel, rightPanel);

        // ── ADD BUTTON ACTION ─────────────────────────────────────
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                // Shake effect — red border
                nameField.setStyle(nameField.getStyle() +
                        "-fx-border-color: #EF4444; -fx-border-radius: 8;");
                Notification.message("Name cannot be empty.");
                return;
            }

            // Reset border
            nameField.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #E5E7EB;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 9 12 9 12;
                -fx-font-size: 13px;
                """);

            String gender = genderBox.getValue();
            String branch = branchField.getText().trim();
            String phone  = phoneField.getText().trim();
            String email  = emailField.getText().trim();

            // Save to DB
            TeacherDB.addTeacher(name, gender, branch, phone, email);

            // Add to session list
            Teacher added = new Teacher(name, gender);
            sessionAdded.add(added);

            // Update count
            int count = sessionAdded.size();
            countBadge.setText(count + " added this session");
            rightCount.setText(count + " teacher" + (count > 1 ? "s" : ""));

            // Add card to session list
            if (sessionList.getChildren().contains(emptyLabel)) {
                sessionList.getChildren().remove(emptyLabel);
            }

            HBox addedCard = buildAddedCard(count, name, gender, branch);
            sessionList.getChildren().add(0, addedCard); // newest on top

            // Clear form
            nameField.clear();
            branchField.clear();
            phoneField.clear();
            emailField.clear();
            genderBox.setValue("Male");
            nameField.requestFocus();

            // Reset preview
            previewAvatar.setText("?");
            previewName.setText("Name will appear here");
            previewName.setStyle("-fx-font-size:13px; -fx-font-weight:700; -fx-text-fill:#9CA3AF;");
        });

        ScrollPane formScroll = new ScrollPane(formPanel);
        formScroll.setFitToWidth(true);
        formScroll.setPrefWidth(480);
        formScroll.setMinWidth(480);
        formScroll.setMaxWidth(480);
        formScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(formScroll, Priority.ALWAYS);

        // Reassemble mainArea with scroll
        mainArea.getChildren().clear();
        mainArea.getChildren().addAll(formScroll, rightPanel);

        root.getChildren().addAll(topBar, mainArea);
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        return root;
    }

    // ── Session added card ─────────────────────────────────────────
    private static HBox buildAddedCard(int index, String name, String gender, String branch) {

        HBox card = new HBox(12);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        boolean male = "Male".equalsIgnoreCase(gender);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #E5E7EB;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);

        // Index badge
        Label idx = new Label(String.valueOf(index));
        idx.setMinSize(22, 22);
        idx.setMaxSize(22, 22);
        idx.setAlignment(Pos.CENTER);
        idx.setStyle("""
            -fx-background-color: #F3F4F6;
            -fx-text-fill: #9CA3AF;
            -fx-font-size: 10px;
            -fx-font-weight: 700;
            -fx-background-radius: 50%;
            """);

        // Avatar
        Label avatar = new Label(getInitials(name));
        avatar.setMinSize(34, 34);
        avatar.setMaxSize(34, 34);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle(
                "-fx-background-color:" + (male ? "#DBEAFE" : "#EDE9FE") + ";" +
                        "-fx-text-fill:"        + (male ? "#1D4ED8" : "#6D28D9") + ";" +
                        "-fx-font-size:11px; -fx-font-weight:800; -fx-background-radius:50%;"
        );

        // Info
        VBox info = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size:13px; -fx-font-weight:700; -fx-text-fill:#111827;");
        HBox tags = new HBox(6);
        Label gTag = new Label(gender);
        gTag.setStyle(
                "-fx-background-color:" + (male ? "#EFF6FF" : "#F5F3FF") + ";" +
                        "-fx-text-fill:"        + (male ? "#2563EB" : "#7C3AED") + ";" +
                        "-fx-font-size:10px; -fx-font-weight:700;" +
                        "-fx-padding: 2 8 2 8; -fx-background-radius:20;"
        );
        tags.getChildren().add(gTag);
        if (!branch.isEmpty()) {
            Label bTag = new Label(branch);
            bTag.setStyle("""
                -fx-background-color: #F0FDF4;
                -fx-text-fill: #059669;
                -fx-font-size: 10px;
                -fx-font-weight: 700;
                -fx-padding: 2 8 2 8;
                -fx-background-radius: 20;
                """);
            tags.getChildren().add(bTag);
        }
        info.getChildren().addAll(nameLabel, tags);

        // Checkmark
        Label check = new Label("✓");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        check.setStyle("-fx-font-size:14px; -fx-text-fill:#10B981; -fx-font-weight:800;");

        card.getChildren().addAll(idx, avatar, info, sp, check);
        return card;
    }

}