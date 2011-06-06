package com.dafrito.rfe.geom.points;

import com.bluespot.geom.vectors.Vector3d;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.proxies.FauxTemplate_Point;

public class Point_Euclidean extends Point implements ScriptConvertible<FauxTemplate_Point> {

	private final Vector3d point;

	public Point_Euclidean(ScriptEnvironment env, double x, double y, double z) {
		this(env, null, x, y, z);
	}

	public Point_Euclidean(ScriptEnvironment env, String name, double x, double y, double z) {
		super(env, name);
		point = Vector3d.mutable(x, y, z);
	}

	@Override
	public Point.System getSystem() {
		return Point.System.EUCLIDEAN;
	}

	@Override
	public double getX() {
		return this.point.getX();
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
		return this.point.getY();
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
		return this.point.getZ();
	}

	@Override
	public void setZ(double value) {
		this.point.setZ(value);
	}

	public void addZ(double offset) {
		this.point.addZ(offset);
	}

	// ScriptConvertible implementation
	@Override
	public FauxTemplate_Point convert(ScriptEnvironment env) {
		FauxTemplate_Point point = new FauxTemplate_Point(this.getEnvironment());
		point.setPoint(this);
		return point;
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
		if (!(obj instanceof Point_Euclidean)) {
			return false;
		}
		Point_Euclidean other = (Point_Euclidean) obj;
		return this.point.equals(other.point);
	}

	@Override
	public String toString() {
		return String.format("Point_Euclidean[%s]", this.point);
	}
}
