package com.innov8.robusdrawer.draw;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Drawing {

    private final ObservableList<DrawingPoint> points = FXCollections.observableArrayList();
    private final SimpleListProperty<DrawingPoint> drawingPoints = new SimpleListProperty<>(points);
    private final SimpleBooleanProperty toolDown = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<PencilColor> pencilColor = new SimpleObjectProperty<>(PencilColor.NONE);
    private final SimpleDoubleProperty width = new SimpleDoubleProperty(0);
    private final SimpleDoubleProperty height = new SimpleDoubleProperty(0);

    boolean sectionStart;

    public Drawing(double width, double height) {
        setWidth(width);
        setHeight(height);

        toolDown.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && oldValue != newValue) {
                if (newValue) {
                    onStartDrawing();
                } else {
                    onStopDrawing();
                }
            }
        });

        drawingPoints.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && sectionStart && newValue.size() != 0) {
                DrawingPoint point =  newValue.get(newValue.size() - 1);
                point.setBoundaryPoint(true);

                sectionStart = false;
            }
        });
    }

    protected void onStartDrawing() {
        sectionStart = true;
    }

    protected void onStopDrawing() {
        if (!getDrawingPoints().isEmpty()) {
            DrawingPoint point =  getDrawingPoints().get(getDrawingPoints().size() - 1);
            point.setBoundaryPoint(true);
        }
    }

    public void drawToGraphics(GraphicsContext graphicsContext) {
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, width, height);
        graphicsContext.setFill(Color.gray(0, 0.5));

        if (getDrawingPoints().size() < 2) {
            return;
        }

        boolean drawLine = false;
        for (int i = 0; i < getDrawingPoints().size() - 1; i++) {
            DrawingPoint point = getDrawingPoints().get(i);
            DrawingPoint nextPoint = getDrawingPoints().get(i + 1);

            if (point.isBoundaryPoint()) {
                drawLine = !drawLine;
            }

            if (drawLine) {
                graphicsContext.setStroke(nextPoint.getColor().getJavaFXColor());

                graphicsContext.strokeLine(point.getX() * width, point.getY() * height, nextPoint.getX() * width, nextPoint.getY() * height);
            }
        }

        /* Debug
        for (int i = 0; i < getDrawingPoints().size(); i++) {
            DrawingPoint point = getDrawingPoints().get(i);
            if (point.isBoundaryPoint()) {
                graphicsContext.setFill(Color.rgb(255, 0, 0, 0.9));
            } else {
                graphicsContext.setFill(Color.rgb(0, 0, 0, 0.25));
            }

            graphicsContext.fillOval(point.getX() * width - 5, point.getY() * height - 5, 10, 10);
        }

         */
    }

    public void draw(float x, float y) {
        if (getPencilColor() != PencilColor.NONE) {
            setToolDown(true);

            DrawingPoint point = new DrawingPoint(x, y, false, getPencilColor());
            getDrawingPoints().add(point);

            sectionStart = false;
        }
    }

    public void clear() {
        getDrawingPoints().clear();
    }

    public ObservableList<DrawingPoint> getDrawingPoints() {
        return drawingPoints.getValue();
    }

    public ReadOnlyListProperty<DrawingPoint> drawingPointsProperty() {
        return drawingPoints;
    }

    public void liftTool() {
        setToolDown(false);
    }

    public void putDownTool() {
        setToolDown(true);
    }

    public boolean isToolDown() {
        return toolDown.get();
    }

    public void setToolDown(boolean pencilDown) {
        toolDown.set(pencilDown);
    }

    public ReadOnlyBooleanProperty toolDownProperty() {
        return toolDown;
    }

    public PencilColor getPencilColor() {
        return pencilColor.get();
    }

    public SimpleObjectProperty<PencilColor> pencilColorProperty() {
        return pencilColor;
    }

    public void setPencilColor(PencilColor pencilColorProperty) {
        this.pencilColor.set(pencilColorProperty);
    }

    public double getWidth() {
        return width.get();
    }

    public SimpleDoubleProperty widthProperty() {
        return width;
    }

    public void setWidth(double widthProperty) {
        this.width.set(widthProperty);
    }

    public double getHeight() {
        return height.get();
    }

    public SimpleDoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double heightProperty) {
        this.height.set(heightProperty);
    }
}
