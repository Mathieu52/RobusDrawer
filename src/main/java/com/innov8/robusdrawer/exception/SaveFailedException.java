package com.innov8.robusdrawer.exception;

import javafx.scene.control.Alert;

public class SaveFailedException extends AlertThrowingException {
    private String message;

    public SaveFailedException(String message) {
        this.message = message;
    }

    public void showAlert() {
        showAlert(this);
    }

    public static void showAlert(SaveFailedException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Save failed");
        alert.setHeaderText("Save Failed Exception");
        alert.setContentText(exception.message);
        alert.showAndWait();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SaveFailedException: " + message;
    }
}
