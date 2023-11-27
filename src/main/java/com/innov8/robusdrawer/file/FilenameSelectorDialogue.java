package com.innov8.robusdrawer.file;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.Optional;

public class FilenameSelectorDialogue extends Dialog<String> {

    private TextField filenameTextField;

    public FilenameSelectorDialogue() {
        // Set the dialog title
        setTitle("Select Filename");

        // Create a custom dialog pane
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        // Create a label for instructions
        Label label = new Label("Enter a Filename:");

        // Create a text field for the user to enter a filename
        filenameTextField = new TextField();
        filenameTextField.setPromptText("e.g., myFile.txt");

        // Add the label and text field to the dialog pane
        vbox.getChildren().addAll(label, filenameTextField);
        getDialogPane().setContent(vbox);

        // Set the result converter to handle the user's choice
        setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType buttonType) {
                if (buttonType == ButtonType.OK) {
                    // Return the entered filename
                    return filenameTextField.getText().trim();
                }
                return null;
            }
        });

        // Add buttons to the dialog pane (OK and Cancel)
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    public Optional<String> showDialog() {
        return showAndWait();
    }
}
