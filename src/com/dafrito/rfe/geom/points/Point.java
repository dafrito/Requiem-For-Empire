package com.dafrito.rfe.geom.points;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public abstract class Point {
	public enum System {
		EUCLIDEAN, SPHERICAL
	}

	public static Point createPoint(Point reference, double x, double y, double z) {
		switch (reference.getSystem()) {
		case EUCLIDEAN:
			return new Point_Euclidean(reference.getEnvironment(), x, y, z);
		case SPHERICAL:
			return new Point_Spherical(reference.getEnvironment(), x, y, z);
		}
		throw new Exception_InternalError("Invalid default");
	}

	private String name;

	private ScriptEnvironment environment;

	public Point(ScriptEnvironment env, String name) {
		this.environment = env;
		this.name = name;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public String getName() {
		return this.name;
	}

	public abstract Point.System getSystem();

	public abstract double getX();

	public abstract double getY();

	public abstract double getZ();

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(double x, double y, double z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public void setPosition(Point point) {
		this.setX(point.getX());
		this.setY(point.getY());
		this.setZ(point.getZ());
	}

	public abstract void setX(double x);

	public abstract void setY(double y);

	public abstract void setZ(double z);

	public void translate(double x, double y, double z) {
		this.setX(this.getX() + x);
		this.setY(this.getY() + y);
		this.setZ(this.getZ() + z);
	}
}
