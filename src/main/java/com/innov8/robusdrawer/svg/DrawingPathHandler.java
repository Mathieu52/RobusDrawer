package com.innov8.robusdrawer.svg;

import com.innov8.robusdrawer.curve.Curve;
import com.innov8.robusdrawer.curve.Ellipse;
import com.innov8.robusdrawer.draw.Drawing;
import com.innov8.robusdrawer.draw.DrawingPoint;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

import java.awt.geom.Point2D;

import static com.innov8.robusdrawer.utils.AngleUtils.normalizeAngle;

public class DrawingPathHandler implements PathHandler {

    public static final float DENSITY = 250;

    Drawing drawing;

    float widthScale, heightScale;

    private float lastX = 0;
    private float lastY = 0;

    private float lastControlPointX = 0;
    private float lastControlPointY = 0;
    public DrawingPathHandler(Drawing drawing, float width, float height) {
        this.drawing = drawing;
        widthScale = height > width ? 1 / height : 1 / width;
        heightScale = width > height ? 1 / width : 1 / height;
    }

    @Override
    public void startPath() throws ParseException {
        lastX = 0;
        lastY = 0;
        drawing.putDownTool();
    }

    @Override
    public void endPath() throws ParseException {
        drawing.liftTool();
    }

    @Override
    public void closePath() throws ParseException {
        for (int i = drawing.getDrawingPoints().size() - 1; i >= 0; i--) {
            DrawingPoint point = drawing.getDrawingPoints().get(i);
            if (point != null && (point.isBoundaryPoint() || i == 0)) {
                lastX = point.getX();
                lastY = point.getY();
                drawing.draw(lastX, lastY);
                break;
            }
        }
    }

    private void draw(double curveLength, PointGenerator generator) {
        int segmentCount = (int) Math.ceil(DENSITY * curveLength);
        for (int i = 1; i <= segmentCount; i++) {
            double t = (double) i / segmentCount;
            Point2D point = generator.generate(t);
            drawing.draw((float) point.getX(), (float) point.getY());
        }
    }

    public void resetLastControlPoint() {
        lastControlPointX = lastX;
        lastControlPointY = lastY;
    }

    @Override
    public void linetoAbs(float x, float y) throws ParseException {
        Point2D.Double start = new Point2D.Double(lastX, lastY);
        Point2D.Double end = new Point2D.Double(x, y);

        Point2D.Double scaledStart = new Point2D.Double(start.x * widthScale, start.y * heightScale);
        Point2D.Double scaledEnd = new Point2D.Double(end.x * widthScale, end.y * heightScale);

        draw(scaledStart.distance(scaledEnd), (t) -> new Point2D.Double(start.x * (1.0 - t) + end.x * t, start.y * (1.0 - t) + end.y * t));

        lastX = x;
        lastY = y;
        resetLastControlPoint();
    }

    @Override
    public void linetoRel(float x, float y) throws ParseException {
       linetoAbs(lastX + x, lastY + y);
    }

    @Override
    public void linetoHorizontalAbs(float x) throws ParseException {
        linetoAbs(x, lastY);
    }

    @Override
    public void linetoHorizontalRel(float x) throws ParseException {
        linetoHorizontalAbs(lastX + x);
    }

    @Override
    public void linetoVerticalAbs(float y) throws ParseException {
        linetoAbs(lastX, y);
    }

    @Override
    public void linetoVerticalRel(float y) throws ParseException {
        linetoVerticalAbs(lastY + y);
    }

    @Override
    public void movetoAbs(float x, float y) throws ParseException {
        drawing.liftTool();
        lastX = x;
        lastY = y;
        drawing.putDownTool();
        resetLastControlPoint();
    }

    @Override
    public void movetoRel(float x, float y) throws ParseException {
        movetoAbs(lastX + x, lastY + y);
    }

    @Override
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        curvetoCubicAbs(lastX + x1, lastY + y1, lastX + x2, lastY + y2, lastX + x, lastY + y);
    }

    @Override
    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
        Point2D.Double startPoint = new Point2D.Double(lastX, lastY);
        Point2D.Double firstControlPoint = new Point2D.Double(x1, y1);
        Point2D.Double secondControlPoint = new Point2D.Double(x2, y2);
        Point2D.Double endPoint = new Point2D.Double(x, y);

        Point2D.Double scaledStartPoint = new Point2D.Double(startPoint.x * widthScale, startPoint.y * heightScale);
        Point2D.Double scaledFirstControlPoint = new Point2D.Double(firstControlPoint.x * widthScale, firstControlPoint.y * heightScale);
        Point2D.Double scaledSecondControlPoint = new Point2D.Double(secondControlPoint.x * widthScale, secondControlPoint.y * heightScale);
        Point2D.Double scaledEndPoint = new Point2D.Double(endPoint.x * widthScale, endPoint.y * heightScale);

        double scaledCurveLength = Curve.bezierCurveLength(scaledStartPoint, scaledFirstControlPoint, scaledSecondControlPoint, scaledEndPoint, 0.1);
        draw(scaledCurveLength, (t) -> Curve.bezierPoint(startPoint, firstControlPoint, secondControlPoint, endPoint, t));

        lastControlPointX = x2;
        lastControlPointY = y2;

        lastX = x;
        lastY = y;
    }

    @Override
    public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
        curvetoCubicRel(lastX - lastControlPointX, lastY - lastControlPointY, x2, y2, x, y);
    }

    @Override
    public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
        curvetoCubicRel(2 * lastX - lastControlPointX, 2 * lastY - lastControlPointY, x2, y2, x, y);
    }

    @Override
    public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
        curvetoCubicRel(x1, y1, x1, y1, x, y);
    }

    @Override
    public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
        curvetoCubicAbs(x1, y1, x1, y1, x, y);
    }

    @Override
    public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
        curvetoQuadraticRel(lastX - lastControlPointX, lastY - lastControlPointY, x, y);
    }

    @Override
    public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
        curvetoQuadraticAbs(2 * lastX - lastControlPointX,  2 * lastY - lastControlPointY, x, y);
    }

    @Override
    public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
       arcAbs(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, lastX + x, lastY + y);
    }

    @Override
    public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
        xAxisRotation *= 180.0 / Math.PI;
        if (rx == 0 && ry == 0) {
            linetoAbs(x, y);
            return;
        }

        rx = Math.abs(rx);
        ry = Math.abs(ry);

        Ellipse.CenterParameters centerParameters = Ellipse.getCenterParameters(lastX, lastY, x, y, largeArcFlag ? 1 : 0, sweepFlag ? 1 : 0, rx, ry, xAxisRotation);

        double theta = centerParameters.theta;
        double dTheta = (normalizeAngle(centerParameters.dTheta / 2.0 + Math.PI) - Math.PI) * 2;
        //double theta = Math.atan2(lastY - centerParameters.cy, lastY - centerParameters.cx) - xAxisRotation;
        //double dTheta = smallestSignedAngle(theta, Math.atan2(x - centerParameters.cy, y - centerParameters.cx) - xAxisRotation);

        //if (!largeArcFlag && dTheta > Math.PI) {
            //dTheta -= 2 * Math.PI;
        //} else if (largeArcFlag && dTheta <= Math.PI) {
           // dTheta += 2 * Math.PI;
       // }

        /*
        if ((largeArcFlag && Math.abs(dTheta) < Math.PI) || (!largeArcFlag && Math.abs(dTheta) > Math.PI)) {
            if (dTheta > 0) {
                dTheta -= 2 * Math.PI;
            } else {
                dTheta += 2 * Math.PI;
            }
        }

        if ((sweepFlag && dTheta < 0) || (!sweepFlag && dTheta > 0)) {
            theta += dTheta;
            dTheta *= -1;
        }
         */


       //if (largeArcFlag && Math.abs(centerParameters.dTheta) > ) {
            //centerParameters.dTheta = Math.atan2(y - centerParameters.cy, x - centerParameters.cx) - centerParameters.theta;
        //}

        //if (largeArcFlag && d)

        if (!largeArcFlag && dTheta > Math.PI) {
            dTheta -= 2 * Math.PI;
        } else if (largeArcFlag && dTheta <= Math.PI) {
            dTheta += 2 * Math.PI;
        }

        if ((sweepFlag && dTheta < 0) || (!sweepFlag && dTheta > 0)) {
            theta += dTheta;
            dTheta *= -1;
        }

        System.out.println(dTheta);

        double arcLength = Ellipse.getArcLength(centerParameters.cx, centerParameters.cy, rx, ry, xAxisRotation, theta, theta + dTheta, widthScale, heightScale, 0.1);

        double[] pointTest = Ellipse.getEllipsePointForAngle(centerParameters.cx, centerParameters.cy, rx, ry, xAxisRotation, theta);
        double dx = pointTest[0] - lastX;
        double dy = pointTest[1] - lastY;

        float finalRx = rx;
        float finalRy = ry;
        double finalDTheta = dTheta;
        double finalTheta = theta;
        float finalXAxisRotation = xAxisRotation;
        draw(arcLength, (t) -> {
            double[] point = Ellipse.getEllipsePointForAngle(centerParameters.cx, centerParameters.cy, finalRx, finalRy, finalXAxisRotation, finalTheta + finalDTheta * t);
            return new Point2D.Double(point[0], point[1]);
        });

        lastX = x;
        lastY = y;
    }
}
