package com.innov8.robusdrawer.draw.robus;

import com.innov8.robusdrawer.draw.Drawing;
import com.innov8.robusdrawer.draw.DrawingPoint;
import com.innov8.robusdrawer.draw.ErasableDrawing;
import com.innov8.robusdrawer.draw.PencilColor;
import com.innov8.robusdrawer.exception.DeserializationFailedException;
import com.innov8.robusdrawer.exception.SaveFailedException;
import com.innov8.robusdrawer.utils.FileUtils;
import javafx.geometry.Bounds;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.innov8.robusdrawer.utils.FileUtils.loadStringList;
import static com.innov8.robusdrawer.utils.FileUtils.saveString;
import static java.lang.Math.*;

public class RobusDrawing extends ErasableDrawing {
    private DrawingSettings drawingSettings;

    public static final String START_TAG = "DRAWING_START";
    public static final String END_TAG = "DRAWING_END";

    public RobusDrawing(double width, double height) {
        this(width, height, new DrawingSettings());
    }

    public RobusDrawing(double width, double height, DrawingSettings drawingSettings) {
        super(width, height);
        this.drawingSettings = drawingSettings;
    }

    public DrawingSettings getDrawingSettings() {
        return drawingSettings;
    }

    public void setDrawingSettings(DrawingSettings drawingSettings) {
        this.drawingSettings = drawingSettings;
    }

    public DrawingInfo getDrawingInfo() {
        return DrawingInfo.fromDrawing(this);
    }

    public void load(String path) throws DeserializationFailedException {
        RobusDrawing.load(this, path);
    }

    public static void load(RobusDrawing robusDrawing, String path) throws DeserializationFailedException {
        DrawingInfo info = new DrawingInfo();
        DrawingSettings settings = new DrawingSettings();
        List<String> lines = loadStringList(new File(path));

        boolean readingInfo = false;
        boolean infoExtracted = false;
        String infoString = "";

        boolean readingSettings = false;
        boolean settingsExtracted = false;
        String settingsString = "";

        boolean readingDrawing = false;
        boolean drawingExtracted = false;
        List<DrawingPoint> drawingPoints = new ArrayList<>();

        for (String line : lines) {
            // Info deserialization
            if (line.equals(DrawingInfo.START_TAG)) {
                readingInfo = true;
                infoExtracted = false;
                infoString = "";
            }

            if (readingInfo) {
                infoString += line + '\n';
            }

            if (readingInfo && line.equals(DrawingInfo.END_TAG)) {
                readingInfo = false;
                infoExtracted = true;
                info.deserialize(infoString);
            }

            // Settings deserialization
            if (line.equals(DrawingSettings.START_TAG)) {
                readingSettings = true;
                settingsExtracted = false;
                settingsString = "";
            }

            if (readingSettings) {
                settingsString += line + "\n";
            }

            if (readingSettings && line.equals(DrawingSettings.END_TAG)) {
                readingSettings = false;
                settingsExtracted = true;
                settings.deserialize(settingsString);
            }

            // Drawing deserialization
            if (readingDrawing && line.equals(END_TAG)) {
                readingDrawing = false;
                drawingExtracted = true;
            }

            if (readingDrawing) {
                String[] values = line.split(" ");
                if (values.length != 4) {
                    throw new DeserializationFailedException("Drawing points aren't formatted correctly.");
                }

                try {
                    DrawingPoint drawingPoint = new DrawingPoint(0, 0, PencilColor.BLACK);
                    drawingPoint.setX((float) (Float.parseFloat(values[0]) / info.getWidth()));
                    drawingPoint.setY(1.0f - (float) (Float.parseFloat(values[1]) / info.getHeight()));

                    for (PencilColor color : PencilColor.values()) {
                        if (color.getName().equals(values[2])) {
                            drawingPoint.setColor(color);
                        }
                    }
                    drawingPoint.setBoundaryPoint(Boolean.parseBoolean(values[3]));

                    drawingPoints.add(drawingPoint);
                } catch (NumberFormatException e) {
                    throw new DeserializationFailedException("Drawing points aren't formatted correctly.");
                }
            }

            if (infoExtracted && line.equals(START_TAG)) {
                readingDrawing = true;
                drawingExtracted = false;
                drawingPoints.clear();
            }
        }

        if (!infoExtracted) {
            throw new DeserializationFailedException("Drawing informations are missing.");
        }

        if (!drawingExtracted) {
            throw new DeserializationFailedException("Drawing points data is missing.");
        }


        robusDrawing.setWidth(info.getWidth());
        robusDrawing.setHeight(info.getHeight());
        robusDrawing.getDrawingPoints().setAll(drawingPoints);

        robusDrawing.setDrawingSettings(settings);
    }

    public void save(String path) throws SaveFailedException {
        save(path, this.getDrawingPoints().size());
    }

    public void save(String path, int pointsLimit) throws SaveFailedException {
        save(path, this.drawingSettings, pointsLimit);
    }

    public void save(String path, DrawingSettings drawingSettings) throws SaveFailedException {
        save(path, drawingSettings, this.getDrawingPoints().size());
    }

    public void save(String path, DrawingSettings drawingSettings, int pointsLimit) throws SaveFailedException {
        save(path, FileUtils.removeExtension(Path.of(path).getFileName().toString()), drawingSettings, pointsLimit);
    }

    public void save(String path, String name) throws SaveFailedException {
        save(path, name, this.getDrawingPoints().size());
    }

    public void save(String path, String name, int pointsLimit) throws SaveFailedException {
        save(path, name, this.drawingSettings, pointsLimit);
    }

    public void save(String path, String name, DrawingSettings drawingSettings) throws SaveFailedException {
        save(path, name, drawingSettings, getDrawingPoints().size());
    }

    public void save(String path, String name, DrawingSettings drawingSettings, int pointsLimit) throws SaveFailedException {
        if (pointsLimit < 0) {
            throw new SaveFailedException("Tried to save Robus drawing but was unable to save a negative number of points.");
        }

        ArrayList<DrawingPoint> reducedPoints = new ArrayList<>();

        for (DrawingPoint point : getDrawingPoints()) {
            reducedPoints.add((DrawingPoint) point.clone());
        }

        int boundaryCount = 0;

        ArrayList<DrawingPoint> nonBoundaryPoints = new ArrayList<>();

        for (DrawingPoint point : reducedPoints) {
            if (!point.isBoundaryPoint()) {
                nonBoundaryPoints.add(point);
            } else {
                boundaryCount++;
            }
        }

        int limit = min(nonBoundaryPoints.size(), pointsLimit - boundaryCount);

        if (boundaryCount > limit) {
            throw new SaveFailedException("Tried to save Robus drawing but was unable to reduce to the requested point amount, too many lines");
        }

        ArrayList<DrawingPoint> toRemove = new ArrayList<>();

        float spacing = (float) nonBoundaryPoints.size() / (float) limit;

        for (int i = 0; i < nonBoundaryPoints.size(); i++) {
            if (i % spacing >= 1 && (reducedPoints.size() - toRemove.size()) > limit) {
                toRemove.add(nonBoundaryPoints.get(i));
            }
        }

        reducedPoints.removeAll(toRemove);

        DrawingInfo drawingInfo = DrawingInfo.fromDrawing(name, this);
        drawingInfo.setPointsCount(reducedPoints.size());

        String fileData = drawingInfo.serialize() + "\n" + drawingSettings.serialize() + "\n";

        fileData += START_TAG + '\n';
        for (DrawingPoint point : reducedPoints) {
            double x = point.getX() * getWidth();
            double y = (1 - point.getY()) * getHeight();
            fileData += String.format(Locale.US, "%f", x) + " ";
            fileData += String.format(Locale.US, "%f", y) + " ";
            fileData += point.getColor().getName().trim() + " ";
            fileData += point.isBoundaryPoint() + "\n";
        }
        fileData += END_TAG + '\n';

        try {
            saveString(new File(path), fileData);
        } catch (RuntimeException e) {
            throw new SaveFailedException(e.getMessage());
        }
    }
}
