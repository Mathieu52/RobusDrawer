package com.innov8.robusdrawer.dialog;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoadingDialog {

    private Dialog<Boolean> dialog;
    private ProgressBar progressBar;
    private Label loadingLabel;
    private Button closeButton;

    public LoadingDialog(Stage ownerStage) {
        // Create a dialog for the loading screen
        dialog = new Dialog<>();

        // Set the owner stage to make it a modal dialog
        dialog.initOwner(ownerStage);
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Set up the content of the dialog
        loadingLabel = new Label("Loading...");
        progressBar = new ProgressBar();
        closeButton = new Button("Close");

        VBox content = new VBox(10);
        content.setMinWidth(200);

        content.getChildren().addAll(loadingLabel, progressBar, createButtonBox());
        content.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(content);
        dialog.setResult(true);

        // Customize the dialog appearance
        dialog.setTitle("Please Wait");
        dialog.setHeaderText(null); // Remove header text
        dialog.setGraphic(null); // Remove graphic

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        closeButton.setDisable(true); // Disabled by default

        return buttonBox;
    }

    public void show() {
        // Show the dialog
        dialog.show();
    }

    public void startService(Service<?> service, Runnable onCompletion) {
        // Bind the progress bar to the service's progress
        progressBar.progressProperty().bind(service.progressProperty());
        closeButton.setOnAction((v) -> {
            dialog.close();
        });

        // Update the loading label dynamically
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadingLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
            }
        });

        service.setOnFailed(e -> closeButton.setDisable(false));
        service.setOnCancelled(e -> closeButton.setDisable(false));

        // Set up the service to enable the close button when it is finished
        service.setOnSucceeded(e -> {
            closeButton.setDisable(false);
            // Perform additional actions on completion
            if (onCompletion != null) {
                onCompletion.run();
            }
        });

        // Show the loading dialog and start the service
        dialog.show();
        service.start();
    }

    public Button getCloseButton() {
        return closeButton;
    }
}
