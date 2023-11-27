package com.innov8.robusdrawer;

import com.innov8.robusdrawer.ressource.RessourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.io.IOException;

public class DrawerApplication extends Application {

    static DrawerController drawerController;

    static Runnable onStartComplete;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DrawerApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        drawerController = fxmlLoader.getController();
        drawerController.setScene(scene);
        drawerController.setStage(stage);

        stage.setScene(scene);
        stage.show();

        if (onStartComplete != null) {
            onStartComplete.run();
        }
    }

    @Override
    public void stop() throws Exception {
        RessourceManager.clearTempDirectory();
        super.stop();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            onStartComplete = () -> drawerController.loadFile(args[0]);
        }

        launch();
    }

    static {
        try {
            Desktop.getDesktop().setOpenFileHandler(new FileHandler());
        } catch (Exception e) {}
    }

    public static class FileHandler implements OpenFilesHandler {

        @Override
        public void openFiles(OpenFilesEvent e) {
            if (drawerController == null) {
                onStartComplete = () -> drawerController.loadFile(e.getFiles().get(0).getAbsolutePath());
            } else {
                drawerController.loadFile(e.getFiles().get(0).getAbsolutePath());
            }
        }
    }
}