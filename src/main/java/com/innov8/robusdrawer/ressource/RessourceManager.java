package com.innov8.robusdrawer.ressource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.innov8.robusdrawer.utils.FileUtils.clearDirectory;

public class RessourceManager {
    public static final Path APPLICATION_DIRECTORY = Paths.get(System.getProperty("user.home"), ".robusDrawer");
    public static final Path TEMPORARY_DIRECTORY = Paths.get(APPLICATION_DIRECTORY.toString(), "temp");

    public static final Path LOG_DIRECTORY = Paths.get(APPLICATION_DIRECTORY.toString(), "log");
    public static final Path LOG_FILE = Paths.get(LOG_DIRECTORY.toString(), "robusDrawer.log");

    static {
        createTempDirectory();
    }
    public static void createLogFile() {
        try {
            if (!Files.exists(LOG_FILE)) {
                Files.createDirectories(LOG_FILE.getParent());
                Files.createFile(LOG_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTempDirectory() {
        try {
            if (!Files.exists(TEMPORARY_DIRECTORY)) {
                Files.createDirectories(TEMPORARY_DIRECTORY);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearTempDirectory() {
        clearDirectory(TEMPORARY_DIRECTORY.toFile());
    }
}
