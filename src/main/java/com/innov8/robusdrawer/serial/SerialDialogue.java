package com.innov8.robusdrawer.serial;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class SerialDialogue extends Dialog<SerialPort> {

    private ListView<SerialPort> portListView;

    public SerialDialogue(List<SerialPort> ports) {
        // Set the dialog title
        setTitle("Select Serial Port");

        // Create a custom dialog pane
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        // Create a label for instructions
        Label label = new Label("Choose a Serial Port:");

        // Create a list view for displaying available ports
        ObservableList<SerialPort> portItems = FXCollections.observableArrayList(ports);
        portListView = new ListView<>(portItems);
        portListView.setPrefHeight(150);

        // Add the label and list view to the dialog pane
        vbox.getChildren().addAll(label, portListView);
        getDialogPane().setContent(vbox);

        // Set the result converter to handle the user's choice
        setResultConverter(new Callback<ButtonType, SerialPort>() {
            @Override
            public SerialPort call(ButtonType buttonType) {
                if (buttonType == ButtonType.OK) {
                    // Return the selected port
                    return portListView.getSelectionModel().getSelectedItem();
                }
                return null;
            }
        });

        // Add buttons to the dialog pane (OK and Cancel)
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    public static SerialDialogue fromAvailablePorts() {
        return new SerialDialogue(List.of(SerialPort.getCommPorts()));
    }

    public Optional<SerialPort> showDialog() {
        return showAndWait();
    }
}
