package com.dafrito.rfe.geom.points;

import com.bluespot.geom.vectors.Vector3d;

public class EuclideanPoint extends Point {

	private final Vector3d point;

	public EuclideanPoint(double x, double y, double z) {
		this(null, x, y, z);
	}

	public EuclideanPoint(String name, double x, double y, double z) {
		super(name);
		point = Vector3d.mutable(x, y, z);
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	@Override
	public double getX() {
		return this.point.x();
	}

	@Override
	public void setX(double value) {
		this.point.setX(value);
	}

	public void addX(double offset) {
		this.point.addX(offset);
	}

	@Override
	public double getY() {
		return this.point.y();
	}

	@Override
	public void setY(double value) {
		this.point.setY(value);
	}

	public void addY(double offset) {
		this.point.addY(offset);
	}

	@Override
	public double getZ() {
		return this.point.z();
	}

	@Override
	public void setZ(double value) {
		this.point.setZ(value);
	}

	public void addZ(double offset) {
		this.point.addZ(offset);
	}

	@Override
	public int hashCode() {
		return this.point.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof EuclideanPoint)) {
			return false;
		}
		EuclideanPoint other = (EuclideanPoint) obj;
		return this.point.equals(other.point);
	}

	@Override
	public String toString() {
		return String.format("Point_Euclidean[%s]", this.point);
	}
}
