package com.innov8.robusdrawer.draw.robus;

import com.innov8.robusdrawer.draw.Drawing;
import com.innov8.robusdrawer.draw.robus.serialize.RobusSerializable;
import com.innov8.robusdrawer.exception.DeserializationFailedException;


import java.lang.reflect.Field;
import java.util.Locale;

public class DrawingInfo implements RobusSerializable {
    private String name;
    private double width;
    private double height;

    private int pointsCount;

    public static final String START_TAG = "DRAWING_INFO_START";
    public static final String END_TAG = "DRAWING_INFO_END";

    public DrawingInfo(String name, double width, double height, int pointsCount) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.pointsCount = pointsCount;
    }

    public DrawingInfo() {
       this("", 0, 0, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    public static DrawingInfo fromDrawing(Drawing drawing) {
        return fromDrawing("", drawing);
    }

    public static DrawingInfo fromDrawing(String name, Drawing drawing) {
        return new DrawingInfo(name, drawing.getWidth(), drawing.getHeight(), drawing.getDrawingPoints().size());
    }

    public String serialize() {
        String str = START_TAG + '\n';

        str += "name" + " = " + name + '\n';
        str += "width" + " = " + String.format(Locale.US, "%f", width) + '\n';
        str += "height" + " = " + String.format(Locale.US, "%f", height) + '\n';
        str += "pointsCount" + " = " + String.format(Locale.US, "%d", pointsCount) + '\n';

        str += END_TAG + '\n';
        return str;
    }

    @Override
    public void deserialize(String value) throws DeserializationFailedException {
        String[] lines = value.split("\n");

        if (!lines[0].equals(START_TAG) || !lines[lines.length - 1].equals(END_TAG)) {
            throw new DeserializationFailedException(String.format("Start tag (%s) or end tag (%s) are missing.", START_TAG, END_TAG));
        }

        for (int i = 1; i < lines.length - 1; i++) {
            String line = lines[i];

            String substring = line.substring(line.indexOf("=") + 2);
            try {
                if (line.startsWith("name")) {
                    name = line.substring(line.indexOf("=") + 1);
                } else if (line.startsWith("width")) {
                    width = Double.parseDouble(substring);
                } else if (line.startsWith("height")) {
                    height = Double.parseDouble(substring);
                } else if (line.startsWith("pointsCount")) {
                    pointsCount = Integer.parseInt(substring);
                }
            } catch (NumberFormatException e) {
                throw new DeserializationFailedException(String.format("Drawing info contained invalid number on line %d.", i));
            }
        }
    }
}
