package com.dafrito.rfe.geom;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.geom.points.Points;
import com.dafrito.rfe.logging.Logs;

public class RadialGradient<T extends GradientValue<T>> implements Gradient<T> {
	private static final int polygonVertices = 4;
	private T gradientValue;
	private double exponent, radius;
	private Point focus;

	public RadialGradient(Point focus, double radius, double exponent) {
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
	public T getGradientValue() {
		return this.gradientValue;
	}

	@Override
	public T valueAt(Point point) {
		double distance = Points.getDistance(this.getFocus(), point);
		if (Math.abs(distance) > this.getRadius() || this.getExponent() == 0) {
			return this.getGradientValue().sample(0.0d);
		}
		return this.getGradientValue().sample(Math.abs(Math.pow(distance / this.getRadius(), this.getExponent()) - 1.0d));
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
			assert Logs.addNode("Entering sequence. i is at: " + i);
			radius += precision * this.getRadius();
			DiscreteRegion newRegion = new DiscreteRegion();
			newRegion.setProperty(this.getGradientValue().getName(), this.getGradientValue().sample(i));
			for (int j = 0; j < RadialGradient.polygonVertices; j++) {
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
	public void setGradientValue(T gradientValue) {
		this.gradientValue = gradientValue;
	}
}
