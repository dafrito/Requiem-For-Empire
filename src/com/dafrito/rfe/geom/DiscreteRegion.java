package com.dafrito.rfe.geom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.geom.points.EuclideanPoint;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.ScriptEnvironment;

/**
 * A mutable, finite two-dimensional region of space.
 * <p>
 * Regions are mutable and they may be degenerate or invalid. Therefore, one
 * must check for these conditions explicitly before working with regions.
 * <p>
 * This class has a lot of unnecessary state. I'd like to do a couple things to
 * this class:
 * <ul>
 * <li>Remove any non-geometric state entirely, such as the uses of
 * {@link ScriptEnvironment}, {@link Nodeable}, and the property map.
 * <li>Ensure that regions are never invalid or degenerate. This doesn't
 * necessarily mean they're convex.
 * <li>Have a hierarchy of regions, based on characteristics. The lowest-level
 * classes would essentially be a {@code Set<Point>}, and higher-level classes
 * would add functionality (or optimizations) depending on their
 * characteristics.
 * <li>Investigate three-dimensional regions.
 * </ul>
 * 
 * @author Aaron Faanes
 * 
 */
public class DiscreteRegion implements Nodeable {
	public static void paint(Graphics2D g2d, DiscreteRegion transformedRegion, Rectangle bounds, boolean fill) {
		assert Logs.openNode("Discrete-Region Painting", "Painting Discrete Region");
		assert Logs.addNode(transformedRegion);
		Polygons.optimizePolygon(transformedRegion);
		transformedRegion = Polygons.clip(transformedRegion, Polygons.convertToRegion(transformedRegion.getEnvironment(), bounds));
		if (transformedRegion != null) {
			// Draw transformed line
			assert Logs.addSnapNode("Clipped Region", transformedRegion);
			if (fill) {
				if (transformedRegion.getProperty("Color") != null) {
					g2d.setColor((Color) transformedRegion.getProperty("Color"));
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.fillPolygon(Polygons.convertToPolygon(transformedRegion));
			} else {
				if (transformedRegion.getProperty("BorderColor") != null) {
					g2d.setColor((Color) transformedRegion.getProperty("BorderColor"));
				} else if (transformedRegion.getProperty("Color") != null) {
					g2d.setColor((Color) transformedRegion.getProperty("Color"));
				} else {
					g2d.setColor(Color.WHITE);
				}
				g2d.drawPolygon(Polygons.convertToPolygon(transformedRegion));
			}
		}
		assert Logs.closeNode();
	}

	public static DiscreteRegion transform(DiscreteRegion region, Point offset, Rectangle bounds, boolean zoom) {
		assert Logs.openNode("Discrete-Region Transformation", "Transforming Discrete Region");
		assert Logs.addNode(region);
		// Offset translation
		List<Point> points = new ArrayList<Point>(region.getPoints());
		DiscreteRegion transformedRegion = new DiscreteRegion(region.getEnvironment());
		transformedRegion.setProperties(region.getProperties());
		for (Point point : points) {
			transformedRegion.addPoint(transformPoint(region.getEnvironment(), point, offset, bounds, zoom));
		}
		assert Logs.closeNode("Transformed Region", transformedRegion);
		return transformedRegion;
	}

	public static Point transformPoint(ScriptEnvironment env, Point point, Point offset, Rectangle bounds, boolean zoom) {
		double width = (bounds.getWidth() - bounds.getX()) / 2;
		double height = (bounds.getHeight() - bounds.getY()) / 2;
		double ax = point.getX() - offset.getX();
		double ay = point.getY() - offset.getY();
		// Orthographic zoom
		if (zoom) {
			ax = ax * Math.pow(2, offset.getZ());
			ay = ay * Math.pow(2, offset.getZ());
		}
		return new EuclideanPoint(ax + bounds.getX() + width, ay + bounds.getY() + height, 0.0d);
	}

	private final List<Point> points = new ArrayList<Point>();
	private final Set<DiscreteRegion> neighbors = new HashSet<DiscreteRegion>();
	private final Map<DiscreteRegion, Integer> intersectionMap = new HashMap<DiscreteRegion, Integer>();
	private double leftExtreme, rightExtreme, topExtreme, bottomExtreme;
	private Point midPoint, interiorPoint;
	private int version;

	private boolean isOptimized;

	private final Map<String, Object> properties = new HashMap<String, Object>();

	private final ScriptEnvironment environment;

	public DiscreteRegion() {
		this((ScriptEnvironment) null);
	}

	public DiscreteRegion(DiscreteRegion otherRegion) {
		assert Logs.openNode("Discrete Region Creation", "Creating discrete region (Duplicating otherRegion)");
		this.environment = otherRegion.getEnvironment();
		this.points.addAll(otherRegion.getPoints());
		this.leftExtreme = otherRegion.getLeftExtreme();
		this.rightExtreme = otherRegion.getRightExtreme();
		this.topExtreme = otherRegion.getTopExtreme();
		this.bottomExtreme = otherRegion.getBottomExtreme();
		this.isOptimized = otherRegion.isOptimized();
		this.version = otherRegion.getVersion();
		this.properties.putAll(otherRegion.getProperties());
		assert Logs.closeNode("Region created", this);
	}

	public DiscreteRegion(ScriptEnvironment env) {
		assert Logs.openNode("Discrete Region Creation", "Creating discrete region (Script environment provided)");
		if (env == null) {
			assert Logs.addNode("ScriptEnvironment: null");
		} else {
			assert Logs.addNode(env);
		}
		this.environment = env;
		this.resetExtrema();
		assert Logs.closeNode("Region created", this);
	}

	public DiscreteRegion(ScriptEnvironment env, Map<String, Object> prop) {
		assert Logs.openNode("Discrete Region Creation", "Creating discrete region (Script environment and properties provided)");
		this.environment = env;
		this.properties.putAll(prop);
		assert Logs.closeNode("Region created", this);
	}

	public void addNeighbor(DiscreteRegion region) {
		if (region.equals(this)) {
			return;
		}
		assert Logs.addSnapNode("Discrete Region Neighbor Additions", "Adding this region as a neighbor", region);
		this.neighbors.add(region);
	}

	public void addPoint(Point point) {
		if (point == null) {
			return;
		}
		assert Logs.openNode("Discrete Region Point Additions", "Adding point to discrete region (" + point + ")");
		this.testExtrema(point);
		this.points.add(point);
		this.resetIntersectionMap();
		assert Logs.closeNode(this);
	}

	public void addPointAt(int location, Point point) {
		assert Logs.openNode("Discrete Region Point Additions", "Adding point to discrete region (" + point + ") at index (" + location + ")");
		this.testExtrema(point);
		this.points.add(location, point);
		assert Logs.addSnapNode("New point list", this.points);
		this.resetIntersectionMap();
		assert Logs.closeNode();
	}

	public void addRegionNeighbor(DiscreteRegion region) {
		if (region.equals(this)) {
			return;
		}
		assert Logs.openNode("Discrete Region Neighbor Evaluations", "Checking for neighbor status");
		assert Logs.addSnapNode("This region", this);
		assert Logs.addSnapNode("Potential neighbor", region);
		List<Point> regionPoints = region.getPoints();
		for (int i = 0; i < this.points.size(); i++) {
			Point pointA = this.points.get(i);
			Point pointB = this.points.get((i + 1) % this.points.size());
			for (int j = 0; j < regionPoints.size(); j++) {
				Point testPoint = regionPoints.get(j);
				Point otherTestPoint = regionPoints.get((j + 1) % regionPoints.size());
				if (!Polygons.testForColinearity(pointA, pointB, testPoint, otherTestPoint)) {
					continue;
				}
				if (!Polygons.getBoundingRectIntersection(pointA, pointB, testPoint, otherTestPoint)) {
					continue;
				}
				region.addNeighbor(this);
				this.neighbors.add(region);
			}
		}
		assert Logs.closeNode();
	}

	public void addRegionNeighbors(Collection<DiscreteRegion> regions) {
		for (DiscreteRegion region : regions) {
			this.addRegionNeighbor(region);
		}
	}

	public void addRegionToMap(DiscreteRegion region) {
		assert Logs.addSnapNode("Intersection Map Additions", "Adding this region to intersection map", region);
		this.intersectionMap.put(region, region.getVersion());
	}

	public boolean checkClearedRegionMap(DiscreteRegion region) {
		assert Logs.openNode("Checking cleared intersection map for region");
		assert Logs.addNode("If found, this means that the region does not intersect this region and all further testing can be omitted.");
		assert Logs.addSnapNode("Region to find", region);
		if (this.intersectionMap.get(region) == null) {
			assert Logs.closeNode("Region not found");
			return false;
		}
		if (this.intersectionMap.get(region) == region.getVersion()) {
			assert Logs.closeNode("Region found and version is identical.");
			return true;
		} else {
			assert Logs.closeNode("Region's version differs from our saved version, returning false.");
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		DiscreteRegion otherRegion = (DiscreteRegion) o;
		List<Point> pointList = otherRegion.getPoints();
		return (this.points.containsAll(pointList) && pointList.containsAll(this.points));
	}

	public double getBottomExtreme() {
		return this.bottomExtreme;
	}

	public Point getCenter() {
		if (this.midPoint == null) {
			this.midPoint = Point.createPoint(this.points.get(0), this.getLeftExtreme() + ((this.getRightExtreme() - this.getLeftExtreme()) / 2), this.getBottomExtreme() + ((this.getTopExtreme() - this.getBottomExtreme()) / 2), 0.0d);
		}
		return this.midPoint;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public Point getInteriorPoint() {
		return this.getCenter();
		//RiffPolygonToolbox.getMidPointOfLine(RiffPolygonToolbox.getMidPointOfLine(points.get(0), points.get(1)), RiffPolygonToolbox.getMidPointOfLine(points.get(1), points.get(2)));
		//return interiorPoint;
	}

	public double getLeftExtreme() {
		return this.leftExtreme;
	}

	public Set<DiscreteRegion> getNeighbors() {
		return new HashSet<DiscreteRegion>(this.neighbors);
	}

	public List<Point> getPoints() {
		return new ArrayList<Point>(this.points);
	}

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public Object getProperty(String name) {
		return this.properties.get(name);
	}

	public double getRightExtreme() {
		return this.rightExtreme;
	}

	public double getTopExtreme() {
		return this.topExtreme;
	}

	public int getVersion() {
		return this.version;
	}

	@Override
	public int hashCode() {
		return this.points.hashCode();
	}

	public boolean isOptimized() {
		return this.isOptimized;
	}

	// Nodeable implementation
	@Override
	public void nodificate() {
		assert Logs.openNode("Discrete Region (" + this.points.size() + " point(s))");
		assert Logs.addSnapNode("Points (" + this.points.size() + " point(s))", this.points);
		if (this.properties.size() == 1) {
			assert Logs.addSnapNode("Property Map (1 property)", this.properties);
		} else {
			assert Logs.addSnapNode("Property Map (" + this.properties.size() + " properties)", this.properties);
		}
		assert Logs.openNode("Details");
		assert Logs.addNode("Script environment reference: " + this.environment);
		assert Logs.openNode("Bounds");
		assert Logs.addNode("Left bound: " + this.leftExtreme);
		assert Logs.addNode("Right bound: " + this.rightExtreme);
		assert Logs.addNode("Top bound: " + this.topExtreme);
		assert Logs.addNode("Bottom bound: " + this.bottomExtreme);
		assert Logs.closeNode();
		assert Logs.addNode("Version: " + this.version);
		assert Logs.addNode("Optimized: " + this.isOptimized);
		if (this.neighbors != null && this.neighbors.size() > 0) {
			assert Logs.openNode("Neighbors (" + this.neighbors.size() + " neighbor(s))");
			for (DiscreteRegion region : this.neighbors) {
				assert Logs.openNode("Discrete Region (" + region.getPoints().size() + " point(s))");
				assert Logs.addSnapNode("Points (" + region.getPoints().size() + " point(s))", this.points);
				if (this.properties.size() == 1) {
					assert Logs.addSnapNode("Property Map (1 property)", region.getProperties());
				} else {
					assert Logs.addSnapNode("Property Map (" + region.getProperties().size() + " properties)", region.getProperties());
				}
				assert Logs.closeNode();
			}
			assert Logs.closeNode();
		}
		assert Logs.addSnapNode("Intersection Map", this.intersectionMap);
		assert Logs.addNode("Mid-point: " + this.midPoint);
		assert Logs.addNode("Interior point: " + this.interiorPoint);
		assert Logs.closeNode();
		assert Logs.closeNode();
	}

	private void recalculateExtrema() {
		assert Logs.openNode("Extrema Recalculations", "Recalculating extrema");
		this.resetExtrema();
		for (Point point : this.points) {
			this.testExtrema(point);
		}
		assert Logs.closeNode();
	}

	public void recheckNeighbors() {
		Set<DiscreteRegion> neighbors = new HashSet<DiscreteRegion>(this.neighbors);
		this.neighbors.clear();
		this.addRegionNeighbors(neighbors);
	}

	public void removePoint(int pointNum) {
		Point point = this.points.get(pointNum);
		this.removePoint(point);
	}

	public void removePoint(Point point) {
		if (!this.points.contains(point)) {
			return;
		}
		assert Logs.openNode("Discrete Region Point Removals", "Removing this point from discrete region (" + point + ")");
		this.points.remove(point);
		if (point.getX() == this.leftExtreme || point.getX() == this.rightExtreme || point.getY() == this.topExtreme || point.getY() == this.bottomExtreme) {
			this.recalculateExtrema();
		}
		assert Logs.addSnapNode("New point list", this.points);
		this.resetIntersectionMap();
		assert Logs.closeNode();
	}

	public void removeRegionNeighbor(DiscreteRegion region) {
		this.neighbors.remove(region);
	}

	private void resetExtrema() {
		assert Logs.addNode("Extrema Settings", "Resetting extrema.");
		this.leftExtreme = java.lang.Double.POSITIVE_INFINITY;
		this.rightExtreme = java.lang.Double.NEGATIVE_INFINITY;
		this.topExtreme = this.rightExtreme;
		this.bottomExtreme = this.leftExtreme;
	}

	public void resetIntersectionMap() {
		assert Logs.addNode("Resetting discrete region's intersection map");
		this.intersectionMap.clear();
		this.interiorPoint = this.midPoint = null;
		this.version++;
		this.isOptimized = false;
		this.recheckNeighbors();
	}

	public void resetNeighbors() {
		this.neighbors.clear();
	}

	public void reversePoints() {
		assert Logs.addNode("Reversing points");
		Collections.reverse(this.points);
	}

	public void setOptimized(boolean optimized) {
		this.isOptimized = optimized;
	}

	public void setPointList(Collection<Point> pointList) {
		assert Logs.openNode("Setting this discrete region's point list to a provided one");
		assert Logs.addSnapNode("Old point list", this.points);
		this.points.clear();
		this.points.addAll(pointList);
		this.recalculateExtrema();
		this.resetIntersectionMap();
		assert Logs.addSnapNode("New point list", this.points);
		assert Logs.closeNode();
	}

	public void setProperties(Map<String, Object> prop) {
		this.properties.clear();
		this.properties.putAll(prop);
	}

	public void setProperty(String name, Object prop) {
		this.properties.put(name, prop);
	}

	private void testExtrema(Point point) {
		if (point.getX() < this.leftExtreme) {
			assert Logs.addNode("Extrema Settings", "Setting left extrema to (" + point.getX() + ") from (" + this.leftExtreme + ")");
			this.leftExtreme = point.getX();
		}
		if (point.getX() > this.rightExtreme) {
			assert Logs.addNode("Extrema Settings", "Setting right extrema to (" + point.getX() + ") from (" + this.rightExtreme + ")");
			this.rightExtreme = point.getX();
		}
		if (point.getY() < this.bottomExtreme) {
			assert Logs.addNode("Extrema Settings", "Setting bottom extrema to (" + point.getY() + ") from (" + this.bottomExtreme + ")");
			this.bottomExtreme = point.getY();
		}
		if (point.getY() > this.topExtreme) {
			assert Logs.addNode("Extrema Settings", "Setting top extrema to (" + point.getY() + ") from (" + this.topExtreme + ")");
			this.topExtreme = point.getY();
		}
	}
}
