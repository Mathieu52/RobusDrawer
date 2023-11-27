package com.innov8.robusdrawer.draw.robus;

import com.innov8.robusdrawer.draw.robus.serialize.RobusSerializable;
import com.innov8.robusdrawer.exception.DeserializationFailedException;

import java.lang.reflect.Field;
import java.util.Locale;

public class DrawingSettings implements RobusSerializable {
    private double followAngularVelocityScale; /**< Scale factor for angular velocity when following a target. */
    private double followVelocity; /**< Velocity at which the robot follows a target. */
    private double curveTightness; /**< Tightness of the curve when following a target. */

    public static final String START_TAG = "SETTINGS_START";
    public static final String END_TAG = "SETTINGS_END";

    public DrawingSettings(double followAngularVelocityScale, double followVelocity, double curveTightness) {
        this.followAngularVelocityScale = followAngularVelocityScale;
        this.followVelocity = followVelocity;
        this.curveTightness = curveTightness;
    }

    public DrawingSettings() {
        this(Float.NaN, Float.NaN, Float.NaN);
    }

    public double getFollowAngularVelocityScale() {
        return followAngularVelocityScale;
    }

    public void setFollowAngularVelocityScale(double followAngularVelocityScale) {
        this.followAngularVelocityScale = followAngularVelocityScale;
    }

    public double getFollowVelocity() {
        return followVelocity;
    }

    public void setFollowVelocity(double followVelocity) {
        this.followVelocity = followVelocity;
    }

    public double getCurveTightness() {
        return curveTightness;
    }

    public void setCurveTightness(double curveTightness) {
        this.curveTightness = curveTightness;
    }

    public String serialize() {
        String str = START_TAG + '\n';

        str += "followAngularVelocityScale" + " = " + String.format(Locale.US, "%f", followAngularVelocityScale) + '\n';
        str += "followVelocity" + " = " + String.format(Locale.US, "%f", followVelocity) + '\n';
        str += "curveTightness" + " = " + String.format(Locale.US, "%f", curveTightness) + '\n';

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
                if (line.startsWith("followAngularVelocityScale")) {
                    followAngularVelocityScale = Double.parseDouble(substring);
                } else if (line.startsWith("followVelocity")) {
                    followVelocity = Double.parseDouble(substring);
                } else if (line.startsWith("curveTightness")) {
                    curveTightness = Double.parseDouble(substring);
                }
            } catch (NumberFormatException e) {
                throw new DeserializationFailedException(String.format("Settings contained invalid number on line %d.", i));
            }
        }
    }
}
