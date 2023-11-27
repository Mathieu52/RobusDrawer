package com.innov8.robusdrawer.curve;

import java.awt.geom.Point2D;

public class Ellipse {
    public static class EndpointParameters {
        double x1, y1, x2, y2, fa, fs;

        @Override
        public String toString() {
            return "EndpointParameters{" +
                    "x1=" + x1 +
                    ", y1=" + y1 +
                    ", x2=" + x2 +
                    ", y2=" + y2 +
                    ", fa=" + fa +
                    ", fs=" + fs +
                    '}';
        }
    }

    public static class CenterParameters {
        public double cx, cy, theta, dTheta;

        @Override
        public String toString() {
            return "CenterParameters{" +
                    "cx=" + cx +
                    ", cy=" + cy +
                    ", theta=" + theta +
                    ", dTheta=" + dTheta +
                    '}';
        }
    }

    public static double getArcLength(double cx, double cy, double rx, double ry, double phi, double theta, double dTheta, double scaleX, double scaleY, double tolerance) {
        double length = 0.0;
        int numSubdivisions = (int) (1 / tolerance); // Adjust the number of subdivisions for better accuracy

        double[] p0 = getEllipsePointForAngle(cx, cy, rx, ry, phi, theta);
        for (int i = 0; i < numSubdivisions; i++) {
            double t1 = (i + 1.0) / (double) numSubdivisions;
            double[] p1 = getEllipsePointForAngle(cx, cy, rx, ry, phi, theta + dTheta * t1);
            length += Math.sqrt((p1[0] - p0[0]) * (p1[0] - p0[0]) * scaleX * scaleX + (p1[1] - p0[1]) * (p1[1] - p0[1]) * scaleY * scaleY);
            p0 = p1;
        }

        return length;
    }

    public static EndpointParameters getEndpointParameters(double cx, double cy, double rx, double ry, double phi, double theta, double dTheta) {
        double[] point1 = getEllipsePointForAngle(cx, cy, rx, ry, phi, theta);
        double[] point2 = getEllipsePointForAngle(cx, cy, rx, ry, phi, theta + dTheta);

        double fa = Math.abs(dTheta) > Math.PI ? 1 : 0;
        double fs = dTheta > 0 ? 1 : 0;

        EndpointParameters endpointParameters = new EndpointParameters();
        endpointParameters.x1 = point1[0];
        endpointParameters.y1 = point1[1];
        endpointParameters.x2 = point2[0];
        endpointParameters.y2 = point2[1];
        endpointParameters.fa = fa;
        endpointParameters.fs = fs;

        return endpointParameters;
    }

    public static double[] getEllipsePointForAngle(double cx, double cy, double rx, double ry, double phi, double theta) {
        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);

        double M = Math.abs(rx) * Math.cos(theta);
        double N = Math.abs(ry) * Math.sin(theta);

        return new double[]{
                cx + cosPhi * M - sinPhi * N,
                cy + sinPhi * M + cosPhi * N
        };
    }

    public static CenterParameters getCenterParameters(double x1, double y1, double x2, double y2, double fa, double fs, double rx, double ry, double phi) {
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);

        double x = cosPhi * (x1 - x2) / 2 + sinPhi * (y1 - y2) / 2;
        double y = -sinPhi * (x1 - x2) / 2 + cosPhi * (y1 - y2) / 2;

        double px = Math.pow(x, 2);
        double py = Math.pow(y, 2);
        double prx = Math.pow(rx, 2);
        double pry = Math.pow(ry, 2);

        double L = px / prx + py / pry;

        if (L > 1) {
            rx = Math.sqrt(L) * Math.abs(rx);
            ry = Math.sqrt(L) * Math.abs(ry);
        } else {
            rx = Math.abs(rx);
            ry = Math.abs(ry);
        }

        double sign = fa == fs ? -1 : 1;
        double M = Math.sqrt((prx * pry - prx * py - pry * px) / (prx * py + pry * px)) * sign;

        double _cx = M * (rx * y) / ry;
        double _cy = M * (-ry * x) / rx;

        double centerX = cosPhi * _cx - sinPhi * _cy + (x1 + x2) / 2;
        double centerY = sinPhi * _cx + cosPhi * _cy + (y1 + y2) / 2;

        double theta = vectorAngle(new double[]{1, 0}, new double[]{(x - _cx) / rx, (y - _cy) / ry});
        double _dTheta = Math.toDegrees(vectorAngle(new double[]{(x - _cx) / rx, (y - _cy) / ry},
                new double[]{(-x - _cx) / rx, (-y - _cy) / ry})) % 360;

        if (fs == 0 && _dTheta > 0) _dTheta -= 360;
        if (fs == 1 && _dTheta < 0) _dTheta += 360;

        CenterParameters centerParameters = new CenterParameters();
        centerParameters.cx = centerX;
        centerParameters.cy = centerY;
        centerParameters.theta = theta;
        centerParameters.dTheta = Math.toRadians(_dTheta);

        return centerParameters;
    }

    static double vectorAngle(double[] vector1, double[] vector2) {
        double dotProduct = vector1[0] * vector2[0] + vector1[1] * vector2[1];
        double magnitude1 = Math.sqrt(vector1[0] * vector1[0] + vector1[1] * vector1[1]);
        double magnitude2 = Math.sqrt(vector2[0] * vector2[0] + vector2[1] * vector2[1]);

        double cosTheta = dotProduct / (magnitude1 * magnitude2);

        return Math.acos(cosTheta);
    }
}
