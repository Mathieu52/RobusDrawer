package com.innov8.robusdrawer.draw;
import javafx.beans.property.SimpleBooleanProperty;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ErasableDrawing extends Drawing {

    private final SimpleBooleanProperty erasing = new SimpleBooleanProperty(false);

    private boolean erasingStart = false;
    private Point2D.Float previousErasePoint = new Point2D.Float();
    public ErasableDrawing(double width, double height) {
        super(width, height);

        erasing.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (oldValue && !newValue) {
                    onStopDrawing();
                }
                erasingStart = !oldValue && newValue;
            }
        });
    }

    @Override
    public void draw(float x, float y) {
        stopErasing();
        super.draw(x, y);
    }

    public void erase(float x, float y, float radius, ErasingMode mode) {
        setToolDown(true);
        startErasing();

        if (mode == ErasingMode.SINGLE) {
            eraseLine(x, y, x, y, radius);
        } else if (mode == ErasingMode.LINE) {
            previousErasePoint.x = x;
            previousErasePoint.y = y;

            if (!erasingStart) {
                eraseLine(previousErasePoint.x, previousErasePoint.y, x, y, radius);
            }
        }

        erasingStart = false;
    }

    protected static boolean updateForRemoval(DrawingPoint previous, DrawingPoint current, DrawingPoint next, boolean inLine) {
        boolean currentIsBoundary = current.isBoundaryPoint();

        boolean isPartOfLine = inLine || currentIsBoundary;
        boolean lineIsLeft = (inLine && !currentIsBoundary) || (currentIsBoundary && !inLine);
        boolean lineIsRight = inLine;

        if (lineIsRight && next != null) {
            boolean isBoundaryPoint = !next.isBoundaryPoint();
            next.setBoundaryPoint(isBoundaryPoint);
        }
        if (lineIsLeft && previous != null) {
            boolean isBoundaryPoint = !previous.isBoundaryPoint();
            previous.setBoundaryPoint(isBoundaryPoint);
        }

        return false;
    }

    protected void eraseLine(float x1, float y1, float x2, float y2, float radius) {
        ArrayList<DrawingPoint> toRemove = new ArrayList<>();

        boolean inLine = false;
        DrawingPoint previousPoint = null;
        for (int i = 0; i < getDrawingPoints().size(); i++) {
            DrawingPoint point = getDrawingPoints().get(i);
            DrawingPoint nextPoint = i == getDrawingPoints().size() - 1 ? null : getDrawingPoints().get(i + 1);

            if (point.isBoundaryPoint()) {
                inLine = !inLine;
            }

            boolean currentIsBoundary = point.isBoundaryPoint();

            boolean isPartOfLine = inLine || currentIsBoundary;
            boolean lineIsLeft = (inLine && !currentIsBoundary) || (currentIsBoundary && !inLine);
            boolean lineIsRight = inLine;

            if(!isPointInCapsule(point.getX(), point.getY(), x1, y1, x2, y2, radius)) {
                if (!isPartOfLine) {
                    toRemove.add(point);
                } else {
                    previousPoint = point;
                }
                continue;
            }

            inLine = updateForRemoval(previousPoint, point, nextPoint, inLine);

            toRemove.add(point);

        }

        getDrawingPoints().removeAll(toRemove);
    }

    private boolean isPointInCapsule(double x, double y, double x1, double y1, double x2, double y2, double radius) {
        if (x1 == x2 && y1 == y2) {
            return (x - x1) *  (x - x1) + (y - y1) * (y - y1) <= radius * radius;
        }

        double squaredDistance = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        double distance = sqrt(squaredDistance);
        double capsuleDirectionX = (x2 - x1) / distance;
        double capsuleDirectionY = (y2 - y1) / distance;
        double capsuleCenterX = (x1 + x2) / 2.0;
        double capsuleCenterY = (y1 + y2) / 2.0;
        double centerPointDistanceX = x - capsuleCenterX;
        double centerPointDistanceY = y - capsuleCenterY;

        boolean alongCapsuleLength = pow((capsuleDirectionY * centerPointDistanceX - capsuleDirectionX * centerPointDistanceY), 2) <= radius * radius;
        boolean betweenCapsuleCaps = pow((capsuleDirectionX * centerPointDistanceX + capsuleDirectionY * centerPointDistanceY), 2) <= squaredDistance / 4.0;
        boolean closeToFirstCap = (x - x1) *  (x - x1) + (y - y1) * (y - y1) <= radius * radius;
        boolean closeToSecondCap = (x - x2) *  (x - x2) + (y - y2) * (y - y2) <= radius * radius;

        return alongCapsuleLength && betweenCapsuleCaps || closeToFirstCap || closeToSecondCap;
    }

    public void stopErasing() {
        setErasing(false);
    }

    public void startErasing() {
        setErasing(true);
    }

    public boolean isErasing() {
        return erasing.get();
    }

    public SimpleBooleanProperty erasingProperty() {
        return erasing;
    }

    public void setErasing(boolean erasing) {
        this.erasing.set(erasing);
    }
}
