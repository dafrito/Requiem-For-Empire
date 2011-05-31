package com.dafrito.rfe.points;

import com.bluespot.geom.vectors.Vector3d;
import com.dafrito.rfe.script.ScriptEnvironment;

public class Point_Spherical extends Point {
	private static final double LATITUDEMAXIMUM = 180;
	private static final double LONGITUDEMAXIMUM = 360;

	private final Vector3d point;

	public Point_Spherical(ScriptEnvironment env, double longitude, double latitude, double magnitude) {
		this(env, null, longitude, latitude, magnitude);
	}

	public Point_Spherical(ScriptEnvironment env, String name, double longitude, double latitude, double magnitude) {
		super(env, name);
		this.point = Vector3d.mutable(longitude, latitude, magnitude);
	}

	@Override
	public Point.System getSystem() {
		return Point.System.SPHERICAL;
	}

	/**
	 * Simplify the vector such that the underlying point is using the smallest
	 * values.
	 */
	private void simplify() {
		this.point.setX(this.point.getX() % LONGITUDEMAXIMUM);
		this.point.setY(this.point.getY() % LATITUDEMAXIMUM);
	}

	@Override
	public double getX() {
		this.simplify();
		return this.point.getX();
	}

	public double getLatitudeDegrees() {
		return this.getX();
	}

	public double getLatitudeRadians() {
		return Math.toRadians(this.getX());
	}

	@Override
	public void setX(double value) {
		this.point.setX(value);
	}

	public void setLatitudeDegrees(double value) {
		this.setX(value);
	}

	public void setLatitudeRadians(double value) {
		this.setX(Math.toDegrees(value));
	}

	@Override
	public double getY() {
		this.simplify();
		return this.point.getY();
	}

	public double getLongitudeDegrees() {
		return this.getY();
	}

	public double getLongitudeRadians() {
		return Math.toRadians(this.getY());
	}

	@Override
	public void setY(double value) {
		this.point.setY(value);
	}

	public void setLongitudeDegrees(double value) {
		this.point.setY(value);
	}

	public void setLongitudeRadians(double value) {
		this.point.setY(Math.toDegrees(value));
	}

	@Override
	public double getZ() {
		return this.point.getZ();
	}

	@Override
	public void setZ(double z) {
		this.point.setZ(z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Point_Spherical)) {
			return false;
		}
		Point_Spherical other = (Point_Spherical) obj;
		this.simplify();
		other.simplify();
		return this.point.equals(other.point);
	}

	@Override
	public int hashCode() {
		this.simplify();
		return this.point.hashCode();
	}

	@Override
	public String toString() {
		this.simplify();
		StringBuilder builder = new StringBuilder("Point_Spherical[");
		if (this.getName() != null) {
			builder.append('"');
			builder.append(this.getName());
			builder.append("\" ");
		}
		builder.append(this.point);
		return builder.toString();
	}
}
