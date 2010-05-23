package com.dafrito.geom;

import com.dafrito.script.ScriptEnvironment;
import com.dafrito.util.RiffToolbox;

public class Point_Spherical extends Point {
    public static final double LATITUDEMAXIMUM = 180;
    public static final double LONGITUDEMAXIMUM = 360;
    private double latitude;
    private double longitude;
    private double magnitude;

    public Point_Spherical(ScriptEnvironment env, double longitude, double latitude, double magnitude) {
        this(env, null, longitude, latitude, magnitude);
    }

    public Point_Spherical(ScriptEnvironment env, String name, double longitude, double latitude, double magnitude) {
        super(env, name);
        this.longitude = longitude % LONGITUDEMAXIMUM;
        this.latitude = latitude % LATITUDEMAXIMUM;
        this.magnitude = magnitude;
    }

    // Degrees
    public double getLongitudeDegrees() {
        return this.longitude;
    }

    public void setLongitudeDegrees(double longitude) {
        this.longitude = longitude % LONGITUDEMAXIMUM;
    }

    public double getLatitudeDegrees() {
        return this.latitude;
    }

    public void setLatitudeDegrees(double latitude) {
        this.latitude = latitude % LATITUDEMAXIMUM;
    }

    // Radians
    public double getLongitudeRadians() {
        return Math.toRadians(this.longitude);
    }

    public void setLongitudeRadians(double longitude) {
        this.longitude = Math.toDegrees(longitude);
    }

    public double getLatitudeRadians() {
        return Math.toRadians(this.latitude);
    }

    public void setLatitudeRadians(double latitude) {
        this.latitude = Math.toDegrees(latitude);
    }

    // Point implementation
    @Override
    public double getX() {
        return getLongitudeDegrees();
    }

    @Override
    public double getY() {
        return getLatitudeDegrees();
    }

    @Override
    public double getZ() {
        return this.magnitude;
    }

    @Override
    public void setX(double x) {
        setLongitudeDegrees(x);
    }

    @Override
    public void setY(double y) {
        setLatitudeDegrees(y);
    }

    @Override
    public void setZ(double z) {
        this.magnitude = z;
    }

    @Override
    public Point.System getSystem() {
        return Point.System.EUCLIDEAN;
    }

    // Object overloading
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Point_Spherical)) {
            return false;
        }
        Point_Spherical point = ((Point_Spherical)o);
        if(RiffToolbox.areEqual(Point.System.EUCLIDEAN, getY(), 90.0d)
            && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), 90.0d)) {
            return true;
        }
        if(RiffToolbox.areEqual(Point.System.EUCLIDEAN, getY(), -90.0d)
            && RiffToolbox.areEqual(Point.System.EUCLIDEAN, point.getY(), -90.0d)) {
            return true;
        }
        return RiffToolbox.areEqual(Point.System.SPHERICAL, getX(), point.getX())
            && RiffToolbox.areEqual(Point.System.SPHERICAL, getY(), point.getY());
    }

    @Override
    public String toString() {
        String string = new String();
        if(getName() != null) {
            string += getName();
        }
        string += "("
            + this.longitude
            + " degrees longitude, "
            + this.latitude
            + " degrees latitude, "
            + this.magnitude
            + ")";
        return string;
    }
}
