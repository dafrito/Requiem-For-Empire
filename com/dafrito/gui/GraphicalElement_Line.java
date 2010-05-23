package com.dafrito.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.geom.DiscreteRegion;
import com.dafrito.geom.Point;
import com.dafrito.geom.Point_Euclidean;
import com.dafrito.geom.RiffPolygonToolbox;
import com.dafrito.geom.RiffPolygonToolbox.RiffIntersectionPoint;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_Line;
import com.dafrito.util.RiffJavaToolbox;

public class GraphicalElement_Line extends InterfaceElement {
    private Point pointA, pointB;

    public GraphicalElement_Line(ScriptEnvironment env, Point pointA, Point pointB) {
        super(env, null, null);
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public void setPointA(Point point) {
        this.pointA = point;
    }

    public void setPointB(Point point) {
        this.pointB = point;
    }

    @Override
    public Rectangle getDrawingBounds() {
        return new Rectangle(getXAnchor() + getLeftMarginMagnitude() + getLeftBorderMagnitude(), getYAnchor()
            + getTopMarginMagnitude()
            + getTopBorderMagnitude(), getInternalWidth(), getInternalHeight());
    }

    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public void paint(Graphics2D g2d) {
        assert LegacyDebugger.open("Line Painting Operations", "Painting Line Element");
        assert LegacyDebugger.addNode(this);
        Point offset;
        if(getParent() instanceof InterfaceElement_Panel) {
            offset = ((InterfaceElement_Panel)getParent()).getOffset();
        } else {
            offset = new Point_Euclidean(getEnvironment(), getXAnchor(), getYAnchor(), 0.0d);
        }
        // Offset translation
        assert this.pointA != null : "Point A is null in LineElement";
        assert this.pointB != null : "Point B is null in LineElement";
        assert offset != null : "Offset point is null in LineElement";
        double ax = this.pointA.getX() - offset.getX();
        double ay = this.pointA.getY() - offset.getY();
        double bx = this.pointB.getX() - offset.getX();
        double by = this.pointB.getY() - offset.getY();
        // Orthographic zoom
        ax = ax * Math.pow(2, offset.getZ());
        ay = ay * Math.pow(2, offset.getZ());
        bx = bx * Math.pow(2, offset.getZ());
        by = by * Math.pow(2, offset.getZ());
        // Converstion to screen coordinates
        assert getParent() != null;
        assert getParent().getContainerElement() != null;
        assert getParent().getContainerElement().getDrawingBounds() != null;
        double width = (getParent().getContainerElement().getDrawingBounds().getWidth() - getParent().getContainerElement().getDrawingBounds().getX()) / 2;
        double height = (getParent().getContainerElement().getDrawingBounds().getHeight() - getParent().getContainerElement().getDrawingBounds().getY()) / 2;
        // Draw transformed line
        Point translatedPointA = new Point_Euclidean(getEnvironment(), ax
            + getParent().getContainerElement().getDrawingBounds().getX()
            + width, ay + getParent().getContainerElement().getDrawingBounds().getY() + height, 0.0d);
        Point translatedPointB = new Point_Euclidean(getEnvironment(), bx
            + getParent().getContainerElement().getDrawingBounds().getX()
            + width, by + getParent().getContainerElement().getDrawingBounds().getY() + height, 0.0d);
        DiscreteRegion region = RiffJavaToolbox.convertToRegion(
            getEnvironment(),
            getParent().getContainerElement().getDrawingBounds());
        List<RiffIntersectionPoint> intersections = RiffPolygonToolbox.getIntersections(
            translatedPointA,
            translatedPointB,
            region);
        if(intersections.size() == 0
            && !RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA, translatedPointA, region)
            && !RiffPolygonToolbox.getBoundingRectIntersection(translatedPointB, translatedPointB, region)) {
            return;
        }
        if(intersections.size() == 2) {
            translatedPointA = intersections.get(0).getIntersection();
            translatedPointB = intersections.get(1).getIntersection();
        } else if(intersections.size() == 1) {
            if(RiffPolygonToolbox.getBoundingRectIntersection(translatedPointA, translatedPointA, region)) {
                translatedPointB = intersections.get(0).getIntersection();
            } else {
                translatedPointA = intersections.get(0).getIntersection();
            }
        }
        assert LegacyDebugger.addNode("Translated first point: " + translatedPointA);
        assert LegacyDebugger.addNode("Translated second point: " + translatedPointB);
        g2d.draw(new java.awt.geom.Line2D.Double(translatedPointA.getX(), translatedPointA.getY(), translatedPointB.getX(), translatedPointB.getY()));
        assert LegacyDebugger.close();
    }

    // ScriptConvertible and Nodeable implementations
    @Override
    public Object convert() {
        FauxTemplate_Line line = new FauxTemplate_Line(getEnvironment());
        line.setPointA(this.pointA);
        line.setPointB(this.pointB);
        return line;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Line Graphical Element");
        assert LegacyDebugger.addNode("First point: " + this.pointA);
        assert LegacyDebugger.addNode("Second point: " + this.pointB);
        assert LegacyDebugger.close();
        return true;
    }
}
