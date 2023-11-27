package com.innov8.robusdrawer.utils;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    /**
     * Reads the content of a file and returns it as a list of strings.
     *
     * @param file The file to be read.
     * @return A list of strings representing the lines of the file.
     * @throws RuntimeException If there is an error reading the file.
     */
    public static List<String> loadStringList(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            throw new RuntimeException("Error reading file: " + file.getName(), e);
        }
        return lines;
    }

    /**
     * Reads the content of a file and returns it as a single string.
     *
     * @param file The file to be read.
     * @return A string containing the concatenated lines of the file.
     * @throws RuntimeException If there is an error reading the file.
     */
    public static String loadString(File file) {
        List<String> lines = loadStringList(file);
        return String.join("\n", lines);
    }

    /**
     * Writes a list of strings to a file.
     *
     * @param file        The file to which the strings will be written.
     * @param stringList  The list of strings to be written to the file.
     * @throws RuntimeException If there is an error creating or writing to the file.
     */
    public static void saveStringList(File file, List<String> stringList) {
        try {
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file: " + file.getName(), e);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            boolean firstLine = true;
            for (String line : stringList) {
                if (!firstLine) {
                    bw.newLine();
                }
                bw.write(line);
                firstLine = false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + file.getName(), e);
        }
    }

    /**
     * Writes a string to a file.
     *
     * @param file   The file to which the string will be written.
     * @param string The string to be written to the file.
     * @throws RuntimeException If there is an error creating or writing to the file.
     */
    public static void saveString(File file, String string) {
        try {
            if (!file.exists()) {
                Files.createFile(file.toPath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating file: " + file.getName(), e);
        }

        try {
            Files.write(file.toPath(), string.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + file.getName(), e);
        }
    }

    /**
     * Removes the file extension from a given file name.
     *
     * @param fileName The file name with an extension.
     * @return The file name without the extension.
     */
    public static String removeExtension(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Deletes a directory and its contents.
     *
     * @param directory The directory to be deleted.
     * @return True if the directory and its contents are successfully deleted, false otherwise.
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            clearDirectory(directory);
            return directory.delete();
        }

        return false;
    }

    /**
     * Clears the contents of a directory without deleting the directory itself.
     *
     * @param directory The directory whose contents will be cleared.
     * @return True if the contents are successfully cleared, false otherwise.
     */
    public static boolean clearDirectory(File directory) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.exists()) {
                continue;
            }

            if (file.isDirectory()) {
                clearDirectory(file);
            } else {
                boolean deletedFile = file.delete();
                if (!deletedFile) {
                    return false;
                }
            }
        }
        return true;
    }
}
