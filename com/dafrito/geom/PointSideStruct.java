package com.dafrito.geom;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;

public class PointSideStruct implements Nodeable {
    private List<Point> left, right, indeterminates;
    double leftWeight, rightWeight;

    public PointSideStruct() {
        this.left = new LinkedList<Point>();
        this.right = new LinkedList<Point>();
        this.indeterminates = new LinkedList<Point>();
    }

    public PointSideStruct(List<Point> left, List<Point> right, List<Point> indet) {
        this.left = left;
        this.right = right;
        this.indeterminates = indet;
    }

    public void addLeft(Point point, double value) {
        this.left.add(point);
        this.leftWeight += value;
    }

    public void addRight(Point point, double value) {
        this.right.add(point);
        this.rightWeight += value;
    }

    public void addIndeterminate(Point point) {
        this.indeterminates.add(point);
    }

    public List<Point> getLeftPoints() {
        return this.left;
    }

    public List<Point> getRightPoints() {
        return this.right;
    }

    public List<Point> getIndeterminates() {
        return this.indeterminates;
    }

    public boolean isStraddling() {
        return this.left.size() != 0 && this.right.size() != 0;
    }

    public boolean isLessThan() {
        return this.left.size() != 0 && this.right.size() == 0;
    }

    public boolean isGreaterThan() {
        return this.left.size() == 0 && this.right.size() != 0;
    }

    public boolean hasIndeterminates() {
        return !this.indeterminates.isEmpty();
    }

    public boolean isColinear() {
        return this.left.size() == 0 && this.right.size() == 0;
    }

    public void validate() {
        if(Math.abs(this.rightWeight) < Math.pow(10, -5) && this.right.size() > 0) {
            assert LegacyDebugger.addNode("Right-side list is insignificant, clearing.");
            this.right.clear();
        }
        if(Math.abs(this.leftWeight) < Math.pow(10, -5) && this.left.size() > 0) {
            assert LegacyDebugger.addNode("Left-side list is insignificant, clearing.");
            this.left.clear();
        }
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Point-Side Test Results");
        assert LegacyDebugger.addSnapNode(
            "Left-side Points (Weight: " + this.leftWeight + "): " + this.left.size() + " element(s)",
            this.left);
        assert LegacyDebugger.addSnapNode("Right-side Points (Weight: "
            + this.rightWeight
            + "): "
            + this.right.size()
            + " element(s)", this.right);
        assert LegacyDebugger.addSnapNode(
            "Indeterminate Points: " + this.indeterminates.size() + " element(s)",
            this.indeterminates);
        assert LegacyDebugger.close();
        return true;
    }
}