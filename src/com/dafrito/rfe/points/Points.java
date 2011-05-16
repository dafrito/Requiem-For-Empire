package com.dafrito.rfe.points;

import java.util.Random;

import com.dafrito.rfe.Terrestrial;

/**
 * A collection of math utilities.
 * 
 * @author Aaron Faanes
 * 
 */
public final class Points {

	private Points() {
		throw new AssertionError("Cannot be instantiated");
	}

	public final static double DOUBLE_MIN = 1e-10;
	private static Random random;

	// Returns a static Random class.
	public static Random getRandom() {
		if (Points.random == null) {
			Points.random = new Random();
		}
		return Points.random;
	}

	public static boolean isGreaterThan(double greater, double less) {
		return (greater - less > Points.DOUBLE_MIN);
	}

	public static boolean isGreaterThanOrEqualTo(Object point, double greater, double less) {
		if (point instanceof Point_Spherical) {
			return isGreaterThanOrEqualTo(Point.System.SPHERICAL, greater, less);
		}
		return isGreaterThanOrEqualTo(Point.System.EUCLIDEAN, greater, less);
	}

	public static boolean isGreaterThanOrEqualTo(Point.System point, double greater, double less) {
		return (isGreaterThan(greater, less) || areEqual(point, greater, less));
	}

	public static boolean isLessThan(double less, double greater) {
		return (less - greater < -Points.DOUBLE_MIN);
	}

	public static boolean isLessThanOrEqualTo(Object point, double greater, double less) {
		if (point instanceof Point_Spherical) {
			return isLessThanOrEqualTo(Point.System.SPHERICAL, greater, less);
		}
		return isLessThanOrEqualTo(Point.System.EUCLIDEAN, greater, less);
	}

	public static boolean isLessThanOrEqualTo(Point.System point, double less, double greater) {
		return (isLessThan(less, greater) || areEqual(point, less, greater));
	}

	// Converts degress to radians.
	public static double convertDegreesToRadians(double degrees) {
		return degrees * Math.PI / 180;
	}

	public static double getDistance(double ax, double ay, double bx, double by) {
		return Math.sqrt(Math.pow(bx - ax, 2) + Math.pow(by - ay, 2));
	}

	// 2-dimensional distance formula for Points (Fallback distance formula when testing Sphere vs. Euclidean)
	public static double getDistance(Point firstPoint, Point secondPoint) {
		return getDistance(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
	}

	// 3-dimensional distance formula for Point_Euclideans
	public static double getDistance(Point_Euclidean firstPoint, Point_Euclidean secondPoint) {
		//( (x-a)2 + (y - b)2 + (z - c)2 )1/2
		double testDouble = Math.sqrt(Math.pow((secondPoint.getX() - firstPoint.getX()), 2) + Math.pow((secondPoint.getY() - firstPoint.getY()), 2) + Math.pow((secondPoint.getZ() - firstPoint.getZ()), 2));
		return testDouble;
	}

	// Spherical distance formula for Point_Sphericals
	public static double getDistance(Terrestrial terrestrial, Point_Spherical firstPoint, Point_Spherical secondPoint) {
		double firstLat = Math.toRadians(firstPoint.getLatitudeDegrees());
		double secondLat = Math.toRadians(secondPoint.getLatitudeDegrees());
		double firstLong = Math.toRadians(firstPoint.getLongitudeDegrees());
		double secondLong = Math.toRadians(secondPoint.getLongitudeDegrees());
		double temp = Math.pow(Math.sin((secondLat - firstLat) / 2), 2) + Math.cos(firstLat) * Math.cos(secondLat) * Math.pow(Math.sin((secondLong - firstLong) / 2), 2);
		return terrestrial.getRadius() * 2 * Math.asin(Math.min(1, Math.sqrt(temp)));
	}

	public static boolean areEqual(Point point, double a, double b) {
		if (point instanceof Point_Spherical) {
			return areEqual(Point.System.SPHERICAL, a, b);
		}
		return areEqual(Point.System.EUCLIDEAN, a, b);
	}

	public static boolean areEqual(Point.System point, double a, double b) {
		switch (point) {
		case SPHERICAL:
			if ((areEqual(Point.System.EUCLIDEAN, a, 180.0d) || areEqual(Point.System.EUCLIDEAN, a, 0.0d)) && (areEqual(Point.System.EUCLIDEAN, b, 180.0d) || areEqual(Point.System.EUCLIDEAN, b, 0.0d))) {
				return true;
			}
			return (Math.abs(a - b) < Points.DOUBLE_MIN);
		default:
			return (Math.abs(a - b) < Points.DOUBLE_MIN);
		}
	}

}
