package com.planner.GUI;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.util.Optional;

public class Notification extends Application {
    public static Alert message(String mess){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Box");
        alert.setHeaderText("Important Information");
        alert.setContentText(mess);

        DialogPane dialogPane = alert.getDialogPane();

        try {
            String css = Notification.class.getResource("/app.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
            dialogPane.getStyleClass().add("alert-dialog");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        alert.showAndWait();


        return alert;
    }
    public static boolean confirm(String mess) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText("Delete Confirmation");
        alert.setContentText(mess);

        DialogPane dialogPane = alert.getDialogPane();

        try {
            String css = Notification.class.getResource("/app.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
            dialogPane.getStyleClass().add("alert-dialog");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    @Override
    public void start(Stage stage) throws Exception {
        Alert alert = message("Error");

    }
}
