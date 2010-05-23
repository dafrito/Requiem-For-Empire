package com.dafrito.geom;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.gui.Krumflex;
import com.dafrito.util.RiffToolbox;

public class Gradient_Radial implements Gradient {
    private static final int polygonVertices = 4;
    private Krumflex krumflex;
    private double exponent, radius;
    private Point focus;

    public Gradient_Radial(Point focus, double radius, double exponent) {
        this.focus = focus;
        this.radius = radius;
        this.exponent = exponent;
    }

    public Point getFocus() {
        return this.focus;
    }

    public double getRadius() {
        return this.radius;
    }

    public double getExponent() {
        return this.exponent;
    }

    // Gradient implementation
    public void setKrumflex(Krumflex krumflex) {
        this.krumflex = krumflex;
    }

    public Krumflex getKrumflex() {
        return this.krumflex;
    }

    public Krumflex getKrumflexAt(Point point) {
        double distance = RiffToolbox.getDistance(getFocus(), point);
        if(Math.abs(distance) > getRadius() || getExponent() == 0) {
            return getKrumflex().getKrumflexFromIntensity(0.0d);
        }
        return getKrumflex().getKrumflexFromIntensity(Math.abs(Math.pow(distance / getRadius(), getExponent()) - 1.0d));
    }

    public List<DiscreteRegion> getRegions(double precision) {
        List<DiscreteRegion> list = new LinkedList<DiscreteRegion>();
        double calculatedRadius = 0;
        DiscreteRegion lastRegion = null;
        for(double i = 1.0d; i > 0.0d; i -= precision) {
            assert LegacyDebugger.addNode("Entering sequence. i is at: " + i);
            calculatedRadius += precision * getRadius();
            DiscreteRegion newRegion = new DiscreteRegion();
            newRegion.setProperty(getKrumflex().getName(), getKrumflex().getKrumflexFromIntensity(i));
            for(int j = 0; j < Gradient_Radial.polygonVertices; j++) {
                double radianOffset = ((Math.PI * 2) / polygonVertices) * j;
                double longOffset = Math.cos(radianOffset) * calculatedRadius;
                double latOffset = Math.sin(radianOffset) * calculatedRadius;
                newRegion.addPoint(Point.createPoint(getFocus(), getFocus().getX() + longOffset, getFocus().getY()
                    + latOffset, 0.0d));
            }
            if(lastRegion == null) {
                list.add(newRegion);
                lastRegion = newRegion;
            } else {
                DiscreteRegion fullRegion = new DiscreteRegion();
                for(int q = 0; q <= lastRegion.getPoints().size() / 2; q++) {
                    fullRegion.getPoints().add(lastRegion.getPoints().get(q));
                }
                for(int q = 0; q <= newRegion.getPoints().size() / 2; q++) {
                    fullRegion.getPoints().add(newRegion.getPoints().get((newRegion.getPoints().size() / 2) - q));
                }
                lastRegion = newRegion;
                list.add(fullRegion);
            }
        }
        return list;
    }

    public double getLeftExtreme() {
        return getFocus().getX() - getRadius();
    }

    public double getRightExtreme() {
        return getFocus().getX() + getRadius();
    }

    public double getTopExtreme() {
        return getFocus().getY() + getRadius();
    }

    public double getBottomExtreme() {
        return getFocus().getY() - getRadius();
    }
}
