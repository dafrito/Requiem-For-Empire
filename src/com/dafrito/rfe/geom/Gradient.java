package com.dafrito.rfe.geom;

import java.util.List;

import com.dafrito.rfe.geom.points.Point;

public interface Gradient<T extends GradientValue<T>> {
	public double getBottomExtreme();

	public T getGradientValue();

	public T valueAt(Point point);

	public double getLeftExtreme();

	public List<DiscreteRegion> getRegions(double precision);

	public double getRightExtreme();

	public double getTopExtreme();

	public void setGradientValue(T flex);
}
