package com.dafrito.geom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.templates.FauxTemplate_DiscreteRegion;
import com.dafrito.util.RiffJavaToolbox;

public class DiscreteRegion implements Nodeable, ScriptConvertible {
    private List<Point> m_points;
    private Set<DiscreteRegion> m_neighbors;
    private Map<DiscreteRegion, Integer> m_intersectionMap = new HashMap<DiscreteRegion, Integer>();
    private double m_leftExtreme, m_rightExtreme, m_topExtreme, m_bottomExtreme;
    private Point m_midPoint, m_interiorPoint;
    private int m_version;
    private boolean m_isOptimized;
    private Map<String, Object> m_properties;
    private ScriptEnvironment m_environment;

    public DiscreteRegion(ScriptEnvironment env) {
        assert LegacyDebugger.open("Discrete Region Creation", "Creating discrete region (Script environment provided)");
        if(env == null) {
            assert LegacyDebugger.addNode("ScriptEnvironment: null");
        } else {
            assert LegacyDebugger.addNode(env);
        }
        this.m_environment = env;
        this.m_points = new Vector<Point>();
        this.m_neighbors = new HashSet<DiscreteRegion>();
        this.m_properties = new HashMap<String, Object>();
        resetExtrema();
        assert LegacyDebugger.close("Region created", this);
    }

    public DiscreteRegion(ScriptEnvironment env, Map<String, Object> prop) {
        assert LegacyDebugger.open(
            "Discrete Region Creation",
            "Creating discrete region (Script environment and properties provided)");
        this.m_environment = env;
        this.m_points = new Vector<Point>();
        this.m_neighbors = new HashSet<DiscreteRegion>();
        this.m_properties = prop;
        assert LegacyDebugger.close("Region created", this);
    }

    public DiscreteRegion() {
        this((ScriptEnvironment)null);
    }

    public DiscreteRegion(DiscreteRegion otherRegion) {
        assert LegacyDebugger.open("Discrete Region Creation", "Creating discrete region (Duplicating otherRegion)");
        this.m_environment = otherRegion.getEnvironment();
        this.m_neighbors = new HashSet<DiscreteRegion>();
        this.m_points = new Vector<Point>(otherRegion.getPoints());
        this.m_leftExtreme = otherRegion.getLeftExtreme();
        this.m_rightExtreme = otherRegion.getRightExtreme();
        this.m_topExtreme = otherRegion.getTopExtreme();
        this.m_bottomExtreme = otherRegion.getBottomExtreme();
        this.m_isOptimized = otherRegion.isOptimized();
        this.m_version = otherRegion.getVersion();
        this.m_properties = otherRegion.getProperties();
        assert LegacyDebugger.close("Region created", this);
    }

    public ScriptEnvironment getEnvironment() {
        return this.m_environment;
    }

    public void setProperty(String name, Object prop) {
        this.m_properties.put(name, prop);
    }

    public Object getProperty(String name) {
        return this.m_properties.get(name);
    }

    public Map<String, Object> getProperties() {
        return this.m_properties;
    }

    public void setProperties(Map<String, Object> prop) {
        this.m_properties = prop;
    }

    public static DiscreteRegion transform(DiscreteRegion region, Point offset, Rectangle bounds, boolean zoom) {
        assert LegacyDebugger.open("Discrete-Region Transformation", "Transforming Discrete Region");
        assert LegacyDebugger.addNode(region);
        // Offset translation
        List<Point> points = new LinkedList<Point>(region.getPoints());
        DiscreteRegion transformedRegion = new DiscreteRegion(region.getEnvironment());
        transformedRegion.setProperties(region.getProperties());
        for(Point point : points) {
            transformedRegion.addPoint(transformPoint(region.getEnvironment(), point, offset, bounds, zoom));
        }
        assert LegacyDebugger.close("Transformed Region", transformedRegion);
        return transformedRegion;
    }

    public static Point transformPoint(ScriptEnvironment env, Point point, Point offset, Rectangle bounds, boolean zoom) {
        double width = (bounds.getWidth() - bounds.getX()) / 2;
        double height = (bounds.getHeight() - bounds.getY()) / 2;
        double ax = point.getX() - offset.getX();
        double ay = point.getY() - offset.getY();
        // Orthographic zoom
        if(zoom) {
            ax = ax * Math.pow(2, offset.getZ());
            ay = ay * Math.pow(2, offset.getZ());
        }
        return new Point_Euclidean(env, ax + bounds.getX() + width, ay + bounds.getY() + height, 0.0d);
    }

    public static void paint(Graphics2D g2d, DiscreteRegion givenRegion, Rectangle bounds, boolean fill) {
        assert LegacyDebugger.open("Discrete-Region Painting", "Painting Discrete Region");
        assert LegacyDebugger.addNode(givenRegion);
        RiffPolygonToolbox.optimizePolygon(givenRegion);
        DiscreteRegion transformedRegion = RiffPolygonToolbox.clip(givenRegion, RiffJavaToolbox.convertToRegion(
            givenRegion.getEnvironment(),
            bounds));
        if(transformedRegion != null) {
            // Draw transformed line
            assert LegacyDebugger.addSnapNode("Clipped Region", transformedRegion);
            if(fill) {
                if(transformedRegion.getProperty("Color") != null) {
                    g2d.setColor((Color)transformedRegion.getProperty("Color"));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillPolygon(RiffJavaToolbox.convertToPolygon(transformedRegion));
            } else {
                if(transformedRegion.getProperty("BorderColor") != null) {
                    g2d.setColor((Color)transformedRegion.getProperty("BorderColor"));
                } else if(transformedRegion.getProperty("Color") != null) {
                    g2d.setColor((Color)transformedRegion.getProperty("Color"));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.drawPolygon(RiffJavaToolbox.convertToPolygon(transformedRegion));
            }
        }
        assert LegacyDebugger.close();
    }

    public void resetNeighbors() {
        this.m_neighbors.clear();
    }

    public double getLeftExtreme() {
        return this.m_leftExtreme;
    }

    public double getRightExtreme() {
        return this.m_rightExtreme;
    }

    public double getTopExtreme() {
        return this.m_topExtreme;
    }

    public double getBottomExtreme() {
        return this.m_bottomExtreme;
    }

    public Point getCenter() {
        if(this.m_midPoint == null) {
            this.m_midPoint = Point.createPoint(this.m_points.get(0), getLeftExtreme()
                + ((getRightExtreme() - getLeftExtreme()) / 2), getBottomExtreme()
                + ((getTopExtreme() - getBottomExtreme()) / 2), 0.0d);
        }
        return this.m_midPoint;
    }

    public Point getInteriorPoint() {
        return getCenter();
        // RiffPolygonToolbox.getMidPointOfLine(RiffPolygonToolbox.getMidPointOfLine(m_points.get(0),
        // m_points.get(1)),
        // RiffPolygonToolbox.getMidPointOfLine(m_points.get(1),
        // m_points.get(2)));
        // return m_interiorPoint;
    }

    public Set<DiscreteRegion> getNeighbors() {
        return new HashSet<DiscreteRegion>(this.m_neighbors);
    }

    public List<Point> getPoints() {
        return new LinkedList<Point>(this.m_points);
    }

    public void addPoint(Point point) {
        if(point == null) {
            return;
        }
        assert LegacyDebugger.open("Discrete Region Point Additions", "Adding point to discrete region (" + point + ")");
        testExtrema(point);
        this.m_points.add(point);
        resetIntersectionMap();
        assert LegacyDebugger.close(this);
    }

    public void addPointAt(int location, Point point) {
        assert LegacyDebugger.open("Discrete Region Point Additions", "Adding point to discrete region ("
            + point
            + ") at index ("
            + location
            + ")");
        testExtrema(point);
        this.m_points.add(location, point);
        assert LegacyDebugger.addSnapNode("New point list", this.m_points);
        resetIntersectionMap();
        assert LegacyDebugger.close();
    }

    public void reversePoints() {
        assert LegacyDebugger.addNode("Reversing points");
        Collections.reverse(this.m_points);
    }

    public void removePoint(int pointNum) {
        Point point = this.m_points.get(pointNum);
        removePoint(point);
    }

    public void removePoint(Point point) {
        if(!this.m_points.contains(point)) {
            return;
        }
        assert LegacyDebugger.open("Discrete Region Point Removals", "Removing this point from discrete region ("
            + point
            + ")");
        this.m_points.remove(point);
        if(point.getX() == this.m_leftExtreme
            || point.getX() == this.m_rightExtreme
            || point.getY() == this.m_topExtreme
            || point.getY() == this.m_bottomExtreme) {
            recalculateExtrema();
        }
        assert LegacyDebugger.addSnapNode("New point list", this.m_points);
        resetIntersectionMap();
        assert LegacyDebugger.close();
    }

    public void setPointList(List<Point> pointList) {
        assert LegacyDebugger.open("Setting this discrete region's point list to a provided one");
        assert LegacyDebugger.addSnapNode("Old point list", this.m_points);
        this.m_points = pointList;
        recalculateExtrema();
        resetIntersectionMap();
        assert LegacyDebugger.addSnapNode("New point list", this.m_points);
        assert LegacyDebugger.close();
    }

    private void testExtrema(Point point) {
        if(point.getX() < this.m_leftExtreme) {
            assert LegacyDebugger.addNode("Extrema Settings", "Setting left extrema to ("
                + point.getX()
                + ") from ("
                + this.m_leftExtreme
                + ")");
            this.m_leftExtreme = point.getX();
        }
        if(point.getX() > this.m_rightExtreme) {
            assert LegacyDebugger.addNode("Extrema Settings", "Setting right extrema to ("
                + point.getX()
                + ") from ("
                + this.m_rightExtreme
                + ")");
            this.m_rightExtreme = point.getX();
        }
        if(point.getY() < this.m_bottomExtreme) {
            assert LegacyDebugger.addNode("Extrema Settings", "Setting bottom extrema to ("
                + point.getY()
                + ") from ("
                + this.m_bottomExtreme
                + ")");
            this.m_bottomExtreme = point.getY();
        }
        if(point.getY() > this.m_topExtreme) {
            assert LegacyDebugger.addNode("Extrema Settings", "Setting top extrema to ("
                + point.getY()
                + ") from ("
                + this.m_topExtreme
                + ")");
            this.m_topExtreme = point.getY();
        }
    }

    private void resetExtrema() {
        assert LegacyDebugger.addNode("Extrema Settings", "Resetting extrema.");
        this.m_leftExtreme = java.lang.Double.POSITIVE_INFINITY;
        this.m_rightExtreme = java.lang.Double.NEGATIVE_INFINITY;
        this.m_topExtreme = this.m_rightExtreme;
        this.m_bottomExtreme = this.m_leftExtreme;
    }

    private void recalculateExtrema() {
        assert LegacyDebugger.open("Extrema Recalculations", "Recalculating extrema");
        resetExtrema();
        for(int i = 0; i < this.m_points.size(); i++) {
            testExtrema(this.m_points.get(i));
        }
        assert LegacyDebugger.close();
    }

    public int getVersion() {
        return this.m_version;
    }

    public boolean isOptimized() {
        return this.m_isOptimized;
    }

    public void setOptimized(boolean optimized) {
        this.m_isOptimized = optimized;
    }

    public void resetIntersectionMap() {
        assert LegacyDebugger.addNode("Resetting discrete region's intersection map");
        this.m_intersectionMap.clear();
        this.m_interiorPoint = this.m_midPoint = null;
        this.m_version++;
        this.m_isOptimized = false;
        recheckNeighbors();
    }

    public void addRegionToMap(DiscreteRegion region) {
        assert LegacyDebugger.addSnapNode("Intersection Map Additions", "Adding this region to intersection map", region);
        this.m_intersectionMap.put(region, region.getVersion());
    }

    public boolean checkClearedRegionMap(DiscreteRegion region) {
        assert LegacyDebugger.open("Checking cleared intersection map for region");
        assert LegacyDebugger.addNode("If found, this means that the region does not intersect this region and all further testing can be omitted.");
        assert LegacyDebugger.addSnapNode("Region to find", region);
        if(this.m_intersectionMap.get(region) == null) {
            assert LegacyDebugger.close("Region not found");
            return false;
        }
        if(this.m_intersectionMap.get(region) == region.getVersion()) {
            assert LegacyDebugger.close("Region found and version is identical.");
            return true;
        }
        assert LegacyDebugger.close("Region's version differs from our saved version, returning false.");
        return false;
    }

    public void addNeighbor(DiscreteRegion region) {
        if(region.equals(this)) {
            return;
        }
        assert LegacyDebugger.addSnapNode("Discrete Region Neighbor Additions", "Adding this region as a neighbor", region);
        this.m_neighbors.add(region);
    }

    public void addRegionNeighbor(DiscreteRegion region) {
        if(region.equals(this)) {
            return;
        }
        assert LegacyDebugger.open("Discrete Region Neighbor Evaluations", "Checking for neighbor status");
        assert LegacyDebugger.addSnapNode("This region", this);
        assert LegacyDebugger.addSnapNode("Potential neighbor", region);
        List<Point> regionPoints = region.getPoints();
        for(int i = 0; i < this.m_points.size(); i++) {
            Point pointA = this.m_points.get(i);
            Point pointB = this.m_points.get((i + 1) % this.m_points.size());
            for(int j = 0; j < regionPoints.size(); j++) {
                Point testPoint = regionPoints.get(j);
                Point otherTestPoint = regionPoints.get((j + 1) % regionPoints.size());
                if(!RiffPolygonToolbox.testForColinearity(pointA, pointB, testPoint, otherTestPoint)) {
                    continue;
                }
                if(!RiffPolygonToolbox.getBoundingRectIntersection(pointA, pointB, testPoint, otherTestPoint)) {
                    continue;
                }
                region.addNeighbor(this);
                this.m_neighbors.add(region);
            }
        }
        assert LegacyDebugger.close();
    }

    public void removeRegionNeighbor(DiscreteRegion region) {
        this.m_neighbors.remove(region);
    }

    public void recheckNeighbors() {
        Set<DiscreteRegion> neighbors = new HashSet<DiscreteRegion>(this.m_neighbors);
        this.m_neighbors.clear();
        addRegionNeighbors(neighbors);
    }

    public void addRegionNeighbors(Collection<DiscreteRegion> regions) {
        for(DiscreteRegion region : regions) {
            addRegionNeighbor(region);
        }
    }

    @Override
    public boolean equals(Object o) {
        DiscreteRegion otherRegion = (DiscreteRegion)o;
        List<Point> pointList = otherRegion.getPoints();
        return(this.m_points.containsAll(pointList) && pointList.containsAll(this.m_points));
    }

    @Override
    public int hashCode() {
        return this.m_points.hashCode();
    }

    // ScriptConvertible implementation
    public Object convert() {
        FauxTemplate_DiscreteRegion region = new FauxTemplate_DiscreteRegion(getEnvironment(), getEnvironment().getTemplate(
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING).getType());
        region.setRegion(this);
        return region;
    }

    // Nodeable implementation
    public boolean nodificate() {
        assert LegacyDebugger.open("Discrete Region (" + this.m_points.size() + " point(s))");
        assert LegacyDebugger.addSnapNode("Points (" + this.m_points.size() + " point(s))", this.m_points);
        if(this.m_properties.size() == 1) {
            assert LegacyDebugger.addSnapNode("Property Map (1 property)", this.m_properties);
        } else {
            assert LegacyDebugger.addSnapNode("Property Map (" + this.m_properties.size() + " properties)", this.m_properties);
        }
        assert LegacyDebugger.open("Details");
        assert LegacyDebugger.addNode("Script environment reference: " + this.m_environment);
        assert LegacyDebugger.open("Bounds");
        assert LegacyDebugger.addNode("Left bound: " + this.m_leftExtreme);
        assert LegacyDebugger.addNode("Right bound: " + this.m_rightExtreme);
        assert LegacyDebugger.addNode("Top bound: " + this.m_topExtreme);
        assert LegacyDebugger.addNode("Bottom bound: " + this.m_bottomExtreme);
        assert LegacyDebugger.close();
        assert LegacyDebugger.addNode("Version: " + this.m_version);
        assert LegacyDebugger.addNode("Optimized: " + this.m_isOptimized);
        if(this.m_neighbors != null && this.m_neighbors.size() > 0) {
            assert LegacyDebugger.open("Neighbors (" + this.m_neighbors.size() + " neighbor(s))");
            for(DiscreteRegion region : this.m_neighbors) {
                assert LegacyDebugger.open("Discrete Region (" + region.getPoints().size() + " point(s))");
                assert LegacyDebugger.addSnapNode("Points (" + region.getPoints().size() + " point(s))", this.m_points);
                if(this.m_properties.size() == 1) {
                    assert LegacyDebugger.addSnapNode("Property Map (1 property)", region.getProperties());
                } else {
                    assert LegacyDebugger.addSnapNode(
                        "Property Map (" + region.getProperties().size() + " properties)",
                        region.getProperties());
                }
                assert LegacyDebugger.close();
            }
            assert LegacyDebugger.close();
        }
        assert LegacyDebugger.addSnapNode("Intersection Map", this.m_intersectionMap);
        assert LegacyDebugger.addNode("Mid-point: " + this.m_midPoint);
        assert LegacyDebugger.addNode("Interior point: " + this.m_interiorPoint);
        assert LegacyDebugger.close();
        assert LegacyDebugger.close();
        return true;
    }
}
