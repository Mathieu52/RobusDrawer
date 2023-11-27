package com.innov8.robusdrawer.draw;

import com.innov8.robusdrawer.draw.robus.serialize.RobusSerializable;

public class DrawingPoint implements Cloneable {
    private float x;
    private float y;
    private boolean isBoundaryPoint;
    private PencilColor color;

    public DrawingPoint(float x, float y, boolean isBoundaryPoint, PencilColor color) {
        this.x = x;
        this.y = y;
        this.isBoundaryPoint = isBoundaryPoint;
        this.color = color;
    }

    public DrawingPoint(float x, float y, PencilColor color) {
        this(x, y, false, color);
    }

    public DrawingPoint(float x, float y) {
        this(x, y, PencilColor.NONE);
    }

    public void setPoint(float x, float y) {
        setX(x);
        setY(y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isBoundaryPoint() {
        return isBoundaryPoint;
    }

    public void setBoundaryPoint(boolean boundaryPoint) {
        isBoundaryPoint = boundaryPoint;
    }

    public PencilColor getColor() {
        return color;
    }

    public void setColor(PencilColor color) {
        this.color = color;
    }

    @Override
    public Object clone() {
        return new DrawingPoint(x, y, isBoundaryPoint, color);
    }
}
