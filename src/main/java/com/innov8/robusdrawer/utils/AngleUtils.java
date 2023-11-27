package com.innov8.robusdrawer.utils;

import static java.lang.Math.PI;

public class AngleUtils {
    public static double smallestSignedAngle(double currentAngle, double targetAngle) {
        // Ensure both angles are in the range [0, 2*PI]
        currentAngle = normalizeAngle(currentAngle);
        targetAngle = normalizeAngle(targetAngle);

        // Calculate the difference between the angles
        double angleDifference = targetAngle - currentAngle;

        // Normalize the angle difference to be in the range [-PI, PI]
        if (angleDifference > PI) {
            angleDifference -= 2 * PI;
        } else if (angleDifference < -PI) {
            angleDifference += 2 * PI;
        }

        return angleDifference;
    }

    // Helper function to normalize an angle to the range [0, 2*PI]
    public static double normalizeAngle(double angle) {
        while (angle < 0) {
            angle += 2 * PI;
        }
        while (angle >= 2 * PI) {
            angle -= 2 * PI;
        }
        return angle;
    }
}
