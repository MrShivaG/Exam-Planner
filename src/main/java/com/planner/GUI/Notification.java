package com.planner.GUI;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Notification extends Application {
    public static Alert message(String mess){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message Box");
        alert.setHeaderText("Important Information");
        alert.setContentText(mess);

        alert.showAndWait();
        return alert;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Alert alert = message("Error");

    }
}
