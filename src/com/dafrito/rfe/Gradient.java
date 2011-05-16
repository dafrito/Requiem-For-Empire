package com.dafrito.rfe;

import com.dafrito.rfe.points.Point;

public interface Gradient {
	public double getBottomExtreme();

	public Krumflex getKrumflex();

	public Krumflex getKrumflexAt(Point point);

	public double getLeftExtreme();

	public java.util.List<DiscreteRegion> getRegions(double precision);

	public double getRightExtreme();

	public double getTopExtreme();

	public void setKrumflex(Krumflex flex);
}
