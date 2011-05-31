package com.dafrito.rfe.geom;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.points.Point;
import com.dafrito.rfe.points.Points;


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

	@Override
	public double getBottomExtreme() {
		return this.getFocus().getY() - this.getRadius();
	}

	public double getExponent() {
		return this.exponent;
	}

	public Point getFocus() {
		return this.focus;
	}

	@Override
	public Krumflex getKrumflex() {
		return this.krumflex;
	}

	@Override
	public Krumflex getKrumflexAt(Point point) {
		double distance = Points.getDistance(this.getFocus(), point);
		if (Math.abs(distance) > this.getRadius() || this.getExponent() == 0) {
			return this.getKrumflex().getKrumflexFromIntensity(0.0d);
		}
		return this.getKrumflex().getKrumflexFromIntensity(Math.abs(Math.pow(distance / this.getRadius(), this.getExponent()) - 1.0d));
	}

	@Override
	public double getLeftExtreme() {
		return this.getFocus().getX() - this.getRadius();
	}

	public double getRadius() {
		return this.radius;
	}

	@Override
	public List<DiscreteRegion> getRegions(double precision) {
		List<DiscreteRegion> list = new LinkedList<DiscreteRegion>();
		double radius = 0;
		DiscreteRegion lastRegion = null;
		for (double i = 1.0d; i > 0.0d; i -= precision) {
			assert Debugger.addNode("Entering sequence. i is at: " + i);
			radius += precision * this.getRadius();
			DiscreteRegion newRegion = new DiscreteRegion();
			newRegion.setProperty(this.getKrumflex().getName(), this.getKrumflex().getKrumflexFromIntensity(i));
			for (int j = 0; j < Gradient_Radial.polygonVertices; j++) {
				double radianOffset = ((Math.PI * 2) / polygonVertices) * j;
				double longOffset = Math.cos(radianOffset) * radius;
				double latOffset = Math.sin(radianOffset) * radius;
				newRegion.addPoint(Point.createPoint(this.getFocus(), this.getFocus().getX() + longOffset, this.getFocus().getY() + latOffset, 0.0d));
			}
			if (lastRegion == null) {
				list.add(newRegion);
				lastRegion = newRegion;
			} else {
				DiscreteRegion fullRegion = new DiscreteRegion();
				for (int q = 0; q <= lastRegion.getPoints().size() / 2; q++) {
					fullRegion.getPoints().add(lastRegion.getPoints().get(q));
				}
				for (int q = 0; q <= newRegion.getPoints().size() / 2; q++) {
					fullRegion.getPoints().add(newRegion.getPoints().get((newRegion.getPoints().size() / 2) - q));
				}
				lastRegion = newRegion;
				list.add(fullRegion);
			}
		}
		return list;
	}

	@Override
	public double getRightExtreme() {
		return this.getFocus().getX() + this.getRadius();
	}

	@Override
	public double getTopExtreme() {
		return this.getFocus().getY() + this.getRadius();
	}

	// Gradient implementation
	@Override
	public void setKrumflex(Krumflex krumflex) {
		this.krumflex = krumflex;
	}
}
