package com.innov8.robusdrawer.exception;

import javafx.scene.control.Alert;

public class DeserializationFailedException extends AlertThrowingException {
    private String message;

    public DeserializationFailedException(String message) {
        this.message = message;
    }

    public void showAlert() {
        showAlert(this);
    }

    public static void showAlert(DeserializationFailedException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Deserialization Failed");
        alert.setHeaderText("Deserialization failed exception");
        alert.setContentText(exception.message);
        alert.showAndWait();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DeserializationFailedException: " + message;
    }
}
