package com.dafrito.rfe;

import java.awt.Color;
import java.awt.Polygon;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import com.dafrito.rfe.points.Point;
import com.dafrito.rfe.points.Point_Euclidean;

public class Geometry {
	// Converts a Java-point to a RiffPoint
	public static Point convertPointToEuclidean(ScriptEnvironment env, java.awt.Point point) {
		return new Point_Euclidean(env, point.getX(), point.getY(), 0);
	}

	public static Color getDiscreteRegionColor(DiscreteRegion region) {
		if (region.getProperty("Color") != null) {
			return (Color) region.getProperty("Color");
		}
		if (region.getProperty("Terrain") == null) {
			return Color.WHITE;
		}
		Terrain terrain = (Terrain) region.getProperty("Terrain");
		return new Color(0.8f - .7f * (float) terrain.getBrushDensity(), 1.0f, 0.0f);
	}

	public static java.awt.geom.Line2D.Double getLineFromPoints(Point pointA, Point pointB) {
		return new java.awt.geom.Line2D.Double(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY());
	}

	// Creates a list of lines from the discreteRegion
	public static List<java.awt.geom.Line2D.Double> getLineListFromDiscreteRegion(DiscreteRegion region) {
		List<java.awt.geom.Line2D.Double> list = new LinkedList<java.awt.geom.Line2D.Double>();
		for (int i = 0; i < region.getPoints().size(); i++) {
			Point pointA = region.getPoints().get(i);
			Point pointB = region.getPoints().get((i + 1) % region.getPoints().size());
			list.add(new java.awt.geom.Line2D.Double(pointA.getX(), pointA.getY(), pointB.getX(), pointB.getY()));
		}
		return list;
	}

	// Creates a Java-displayable polygon from the discreteRegion
	public static Polygon getPolygonFromDiscreteRegion(JPanel panel, DiscreteRegion region) {
		Polygon poly = new Polygon();
		List<Point> pointList = region.getPoints();
		for (int i = 0; i < pointList.size(); i++) {
			poly.addPoint((int) (pointList.get(i)).getX(), panel.getHeight() - (int) (pointList.get(i)).getY());
		}
		return poly;
	}
}
