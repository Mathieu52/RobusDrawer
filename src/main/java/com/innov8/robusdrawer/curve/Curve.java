package com.innov8.robusdrawer.curve;

import java.awt.geom.Point2D;

public class Curve {
    public static double bezierCurveLength(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, double tolerance) {
        double length = 0.0;
        double t0 = 0.0;
        int numSubdivisions = (int) (1 / tolerance); // Adjust the number of subdivisions for better accuracy

        Point2D.Double previousPoint = (Point2D.Double) p0.clone();
        for (int i = 0; i < numSubdivisions; i++) {
            double t1 = (i + 1.0) / (double) numSubdivisions;
            Point2D.Double point = bezierPoint(p0, p1, p2, p3, t1);
            length += point.distance(previousPoint);
            previousPoint = point;
        }

        return length;
    }
    // Function to calculate the arc length between two parameters using adaptive subdivision
    private static double arcLength(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, double t0, double t1, double tolerance) {
        double mid = (t0 + t1) / 2.0;

        Point2D.Double p0_mid = bezierPoint(p0, p1, p2, p3, (t0 + mid) / 2.0);
        Point2D.Double p3_mid = bezierPoint(p0, p1, p2, p3, (mid + t1) / 2.0);
        Point2D.Double p_mid = bezierPoint(p0, p1, p2, p3, mid);

        double l0 = p0.distance(p0_mid);
        double l1 = p0_mid.distance(p_mid);
        double l2 = p_mid.distance(p3_mid);
        double l3 = p3_mid.distance(bezierPoint(p0, p1, p2, p3, t1));

        double l = l0 + l1 + l2 + l3;

        if (Math.abs(l - (l0 + l3)) > tolerance) {
            // Subdivide and recursively calculate arc length
            return arcLength(p0, p1, p2, p3, t0, mid, tolerance) + arcLength(p0, p1, p2, p3, mid, t1, tolerance);
        } else {
            // Converged within tolerance, return the length
            return l;
        }
    }

    // Function to calculate a point on the Bezier curve for a given parameter t
    public static Point2D.Double bezierPoint(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, double t) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        Point2D.Double p = new Point2D.Double();
        p.x = uuu * p0.x + 3 * uu * t * p1.x + 3 * u * tt * p2.x + ttt * p3.x;
        p.y = uuu * p0.y + 3 * uu * t * p1.y + 3 * u * tt * p2.y + ttt * p3.y;

        return p;
    }
}
