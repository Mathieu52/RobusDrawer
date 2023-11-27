package com.innov8.robusdrawer.draw;

import javafx.scene.paint.Color;

public enum PencilColor {
    BLACK(Color.rgb(56,56,57), "BLACK"), RED(Color.rgb(245,86,86), "RED"), GREEN(Color.rgb(28,185,129), "GREEN"), BLUE(Color.rgb(57,68,148), "BLUE"), NONE(Color.TRANSPARENT, "NONE");

    private final Color color;
    private final String name;

    PencilColor(Color color, String name) {
        this.color = color;
        this.name = name;
    }

    public Color getJavaFXColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
